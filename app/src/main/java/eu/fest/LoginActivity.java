package eu.fest;

import android.accounts.AccountManager;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.fest.facebook.FacebookManager;
import eu.fest.model.Model;
import eu.fest.model.PushNotification;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements Validator.ValidationListener {

    @App
    ServiceBusApplication app;

    @ViewById
    EditText emailInput;

    @ViewById
    EditText passwordInput;

    @ViewById
    TextView lost_password;

    @ViewById
    TextView other_login;

    @ViewById
    TextView registration_text;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    TextView text;

    private Validator validator;

    private User localuser;

    private Dialog mDialog;

    private FacebookManager fbManager;

    @StringRes(R.string.gcm_key)
    String gcmKey;

    @Extra("data")
    String data;

    public String registrationId;

    LoginActivity activity;

    @ViewById
    TwitterLoginButton twitter_login;

    private static final String TWITTER_KEY = "0SyGUb2V36ygGS56K5KnbHVvr";
    private static final String TWITTER_SECRET = "xT38K3cSCixUcMqVayjBoAA9SJGd5y3t1FdVFW6QQ9LtIYjJpr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        fbManager = new FacebookManager(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        validator = new Validator(this);
        validator.setValidationListener(this);

        this.localuser = new User();
        activity = LoginActivity.this;
    }

    protected void onStart(){
        super.onStart();
        db = new DatabaseManager(this);
        mDialog = new Dialog(LoginActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.message_dialog);
        mDialog.setCancelable(true);
        text = (TextView) mDialog.findViewById(R.id.text);
        Button dialogButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        setupTwitterbtn();
    }

    protected void onStop(){
        super.onStop();
    }

    void setupTwitterbtn(){
        twitter_login.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                localuser.setConnect_social(false);
                localuser.setDeviceId(app.getRegistrationId());
                localuser.setOauth_token(result.data.getAuthToken().token);
                localuser.setOauth_verifier(result.data.getAuthToken().secret);

                text.setText("twitter loggin");
                mDialog.show();

                webServerService.loginWithTwitter(localuser, new retrofit.Callback<CurrentUser>() {
                    @Override
                    public void success(CurrentUser user, Response response) {
                        if (user.isSuccess()) {
                            mDialog.dismiss();
                            app.getSettings().setRememberme(true);
                            //MainActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
                            SyncActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).user(user).start();
                        } else {
                            //text.setText(user.getMessage());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        text.setText(error.getMessage());
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Twittererror", exception.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gcmRegister();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = bundle.getString("data");
            checkPush();
        }
    }

    @Click(R.id.twitter_login_trigger)
    void twitterclickHandle(){
        twitter_login.performClick();
    }

    @Click(R.id.registration)
    void registrationClickHandle(){
        RegistrationActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }

    @Click(R.id.lost_password)
    void lostpwClickHandle(){
        Intent intent = new Intent(this, LostPasswordActivity_.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                LostPasswordActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }
    }

    @Click(R.id.email_login)
    void emailClickHandle() {
        text.setText("loggin");
        mDialog.show();
        validator.validate();
    }

    @Click(R.id.face_login)
    void faceClickHandle(){
        text.setText("Facebook loggin");
        mDialog.show();
        fbManager.me(new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, com.facebook.Response response) {
                String accessToken = fbManager.getActiveSession().getAccessToken();
                loginUserWithAccessToken(accessToken, user);
            }
        });
    }


    @UiThread
    void save() {
        if(isFilled(emailInput)){
            if(isEmailValid(lastEmptyCharDelete(emailInput))) {
                localuser.setEmail(lastEmptyCharDelete(emailInput));
            }else{
                text.setText("Valid email-t adjon meg");
                return;
            }
        }else{
            text.setText("Email megadása kötelező");
            return;
        }

        if (isFilled(passwordInput) && passwordInput.getText().toString().length()>=8) {
            localuser.setPassword(passwordInput.getText().toString());
        }else{
            text.setText("A jelszó kevesebb mint 8 karakter");
            return;
        }

        Log.d("GCM", app.getRegistrationId());

        localuser.setDeviceId(app.getRegistrationId());

        webServerService.loginWithEmail(localuser, new retrofit.Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                if (user.isSuccess()) {
                    mDialog.dismiss();
                    app.getSettings().setRememberme(true);
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject data = new JSONObject(json);
                        if(data.has("message")){
                            JSONObject users = new JSONObject(data.getString("message"));
                            user.setUser_id(Integer.parseInt(users.getString("user_id")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //saveUser(user);
                    //MainActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
                    //FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
                    SyncActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).user(user).start();
                } else {
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject data = new JSONObject(json);
                        if(data.has("message")){
                            String asd = data.getString("message");
                            text.setText(asd);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage().equals("401 Invalid permission")) {
                    localuser.setDeviceId(app.getRegistrationId());
                    webServerService.logout(localuser, new Callback<User>() {
                        @Override
                        public void success(User user, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
                Log.d("Error", error.getMessage());
                text.setText(error.getMessage());
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        save();
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
        }
    }

    @Background
    void loginUserWithAccessToken(String accessToken, GraphUser user) {
        localuser.setAccess_token(accessToken);
        localuser.setConnect_social(false);
        webServerService.loginWithFace(localuser, new retrofit.Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                if (user.isSuccess()) {
                    mDialog.dismiss();
                    app.getSettings().setRememberme(true);
                    //MainActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
                    SyncActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).user(user).start();
                } else {
                    //text.setText(user.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                text.setText(error.getMessage());
            }
        });
        notifyUi(user);
    }

    @UiThread
    void notifyUi(GraphUser user) {
        String lastname = user.getLastName();
        String firstname = user.getFirstName();

        //mDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        /*if (message != null) {
            super.onActivityDefault(responseCode, intent);
        }*/
        Session.getActiveSession().onActivityResult(this, requestCode, responseCode, intent);

        twitter_login.onActivityResult(requestCode, responseCode, intent);
    }

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
    }

    String lastEmptyCharDelete(EditText editText){
        String str = editText.getText().toString();
        if(str.length() > 0) {
            if (str.charAt(str.length() - 1) == ' ') {
                str = str.replace(str.substring(str.length() - 1), "");
            }
            return str;
        }
        return str;
    }

    private void checkPush() {
        if (data != null) {
            ObjectMapper mapper = new ObjectMapper();
            PushNotification push = null;
            try {
                push = mapper.readValue(data, PushNotification.class);
                Log.d("Push message", push.getMessage());
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (push != null) {
                getIntent().removeExtra("data");
            } else {
                //error
            }
        }
    }

    protected void gcmRegister() {
        registrationId = app.getRegistrationId();
        String registrationDevice = app.getRegistrationDevice();
        if (registrationId.isEmpty()) {
            registerGCM();
        }
        if(registrationDevice.isEmpty()){
            setRegistrationDevice();
        }
    }


    void registerGCM() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode != ConnectionResult.SUCCESS && resultCode != ConnectionResult.CANCELED) {
            Log.d("GCM", GooglePlayServicesUtil.getErrorDialog(resultCode, this, 69)+"");
        } else {
            registerInBackground(GoogleCloudMessaging.getInstance(activity));
        }
    }

    @Background
    void registerInBackground(GoogleCloudMessaging gcm) {
        try {
            registrationId = gcm.register(gcmKey);
            localuser.setDeviceId(registrationId);
            app.setRegistrationId(registrationId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Background
    void setRegistrationDevice(){
        String devicename = getDeviceName();
        localuser.setDevice_type(devicename);
        app.setRegistrationDevice(devicename);
    }

    @UiThread
    void notifyUiForDeviceRegistration(Model result) {
        if (result != null && result.isValid()) {
            app.setRegistrationId(registrationId);
            //Crouton.makeText(this, eu.rerisoft.servicebus.R.string.push_registration_success, Style.CONFIRM).show();
        } else {
            //Crouton.makeText(this, eu.rerisoft.servicebus.R.string.push_registration_error, Style.ALERT).show();
        }
    }


    public String getDeviceName() {
        String name;
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                name = capitalize(model);
            } else {
                name = capitalize(manufacturer) + " " + model;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return name;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private boolean saveUser(CurrentUser user) {
        try {
            return db.getDatabaseHelper().getUserDataDao().create(user) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
