package eu.fest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.facebook.FacebookManager;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import eu.fest.utils.Settings;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks{

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    private CurrentUser localuser;

    private ProgressDialog mDialog;

    private FacebookManager fbManager;

    @Extra("data")
    String data;

    public String registrationId;

    SettingsActivity activity;

    @ViewById
    Spinner localespiner;

    @ViewById
    Spinner languagespiner;

    @ViewById
    TwitterLoginButton twitter_connect;

    @ViewById
    CircleImageView profile_image;

    @ViewById
    TextView user_full_name;

    @ViewById
    TextView email_change_txt;

    @ViewById
    TextView password_change_txt;

    Settings settings;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private HashMap<String, String> availableLocales = new HashMap<String, String>();

    private HashMap<String, String> availableLanguage = new HashMap<String, String>();

    private static final String TWITTER_KEY = "0SyGUb2V36ygGS56K5KnbHVvr";
    private static final String TWITTER_SECRET = "xT38K3cSCixUcMqVayjBoAA9SJGd5y3t1FdVFW6QQ9LtIYjJpr";


    private static final int CAMERA_REQUEST = 1888;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    String path;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fbManager = new FacebookManager(this);

        settings = app.getSettings();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        activity = SettingsActivity.this;
        mDialog = new ProgressDialog(activity);
        mDialog.setCancelable(true);
    }

    @Click(R.id.user_data_edit)
    void userdataClickHandle(){
        Intent intent = new Intent(this, UserDataChangeActivity_.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                UserDataChangeActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }
    }



    @Click(R.id.notification_control)
    void notificationcontrolClickHandle(){
        Intent intent = new Intent(this, NotificationSetActivity_.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                NotificationSetActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }
    }

    @Click(R.id.email_change)
    void emailchangeClickHandle(){
        Intent intent = new Intent(this, ChangeEmailActivity_.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                ChangeEmailActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }
    }

    @Click(R.id.password_change)
    void passwordchangeClickHandle(){
        Intent intent = new Intent(this, PasswordChangeActivity_.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                PasswordChangeActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }
    }

    @Click(R.id.change_image_icon)
    void changeImageClickHandle(){
        dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_change_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageButton camera_choser = (ImageButton) dialog.findViewById(R.id.camera_choser);
        camera_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_camera();
            }
        });

        ImageButton file_choser = (ImageButton) dialog.findViewById(R.id.file_choser);
        file_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path="";
                dialog.dismiss();
            }
        });

        Button btn_change = (Button) dialog.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageToServer();
            }
        });

        //dialog.getWindow().setLayout(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
        dialog.show();
    }



    @AfterViews
    void setupUi(){
        setLocale();
        setLanguage();

        db = new DatabaseManager(this);
        for(CurrentUser user : getUser()){
            localuser = user;
        }

        if(localuser.getProfileImage()!=null) {
            Picasso.with(SettingsActivity.this).load(localuser.getProfileImage().startsWith("http") ? localuser.getProfileImage() : "http://" + localuser.getProfileImage()).into(profile_image);
        }

        user_full_name.setText(localuser.getFirstName() + " " + localuser.getLastName());

        if(localuser.getEmail()!=null){
            email_change_txt.setText(getResources().getString(R.string.email_change));
        }else {
            email_change_txt.setText(getResources().getString(R.string.email_link));
        }
        if(app.getSettings().getHavepassword()){
            password_change_txt.setText(getResources().getString(R.string.password_change));
        }else {
            password_change_txt.setText(getResources().getString(R.string.password_link));
        }
    }


    void setLanguage(){
        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        final String currentLanguage = SettingsActivity.this.getResources().getConfiguration().locale.getLanguage();
        availableLanguage.put(getResources().getString(R.string.hungarian), "hu");
        availableLanguage.put(getResources().getString(R.string.english), "en");
        availableLanguage.put(getResources().getString(R.string.german), "de");
        int currentIndex = 0, i = 0;
        for (Map.Entry<String, String> locale: availableLanguage.entrySet()) {
            languageAdapter.add(locale.getKey());
            if (locale.getValue().equals(currentLanguage)) {
                currentIndex = i;
            }
            i++;
        }
        languagespiner.setAdapter(languageAdapter);
        languagespiner.setSelection(currentIndex);

        languagespiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String language = languageAdapter.getItem(i);
                if (!availableLanguage.get(language).equals(currentLanguage)) {
                    setLocale(availableLanguage.get(language));
                    app.getSettings().setLanguage(availableLanguage.get(language));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    void setLocale(){

        final ArrayAdapter<String> localeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        final String currentLocale = SettingsActivity.this.getResources().getConfiguration().locale.getCountry();
        availableLocales.put(getResources().getString(R.string.hungarian), "HU");
        availableLocales.put(getResources().getString(R.string.german), "DE");
        int currentIndex = 0, i = 0;
        for (Map.Entry<String, String> locale: availableLocales.entrySet()) {
            localeAdapter.add(locale.getKey());
            if (locale.getValue().equals(currentLocale)) {
                currentIndex = i;
            }
            i++;
        }
        localespiner.setAdapter(localeAdapter);
        localespiner.setSelection(currentIndex);

        localespiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String locale = localeAdapter.getItem(i);
                if (!availableLocales.get(locale).equals(currentLocale)) {
                    app.getSettings().setLocale(availableLocales.get(locale));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        setupTwitterbtn();
        toolbar_actionbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar_actionbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar_actionbar);
        mNavigationDrawerFragment.setActivity(this);
        mNavigationDrawerFragment.closeDrawer();
        mNavigationDrawerFragment.setWebServerService(webServerService);
        mNavigationDrawerFragment.setServiceBusApplication(app);

        ImageView festival4ever = (ImageView) toolbar_actionbar.findViewById(R.id.festival4ever);
        festival4ever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
            }
        });
    }

    protected void onStop(){
        super.onStop();
    }

    void setupTwitterbtn(){
        twitter_connect.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                User localuser = new User();
                localuser.setDeviceId(app.getRegistrationId());
                localuser.setOauth_token(result.data.getAuthToken().token);
                localuser.setOauth_verifier(result.data.getAuthToken().secret);

                mDialog.setMessage("twitter connect");
                mDialog.show();

                webServerService.connectTwitter(localuser, new retrofit.Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (user.isSuccess()) {
                            mDialog.dismiss();
                        } else {
                            //mDialog.setMessage(user.getMessage());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mDialog.setMessage(error.getMessage());
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Twittererror", exception.getMessage());
            }
        });
    }
    @Click(R.id.twitter_connect_trig)
    void twitterTriggerClickHandle(){
        twitter_connect.performClick();
    }

    @Click(R.id.face_connect)
    void faceClickHandle(){
        mDialog.setMessage("Facebook connect");
        mDialog.show();
        fbManager.me(new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, com.facebook.Response response) {
                String accessToken = fbManager.getActiveSession().getAccessToken();
                loginUserWithAccessToken(accessToken, user);
            }
        });
    }

    @Background
    void loginUserWithAccessToken(String accessToken, GraphUser user) {
        User localuser = new User();
        localuser.setAccess_token(accessToken);
        localuser.setConnect_social(false);
        webServerService.connectFace(localuser, new retrofit.Callback<User>() {
            @Override
            public void success(User user, Response response) {
                if (user.isSuccess()) {
                    mDialog.dismiss();
                    //app.getSettings().setRememberme(true);
                    //MainActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
                } else {
                    //mDialog.setMessage(user.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mDialog.setMessage(error.getMessage());
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, SettingsActivity_.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
    }

    void sendSettingsdata(){
        localuser.setAccept_friend_request(app.getSettings().getAcceptFriendRequest());
        localuser.setAdd_location(app.getSettings().getAddLocation());
        localuser.setChanged_festival_event(app.getSettings().getChangeFestivalEvent());
        localuser.setChanged_festival_event_event(app.getSettings().getChangeFestivalEventEvent());
        localuser.setFriend_going_festival_event(app.getSettings().getFriendGoingFestivalEvent());
        localuser.setFriend_request(app.getSettings().getFriendRequest());
        localuser.setGlobal_notifications(app.getSettings().getGlobalNotification());
        localuser.setLanguage(app.getSettings().getLanguage());
        localuser.setLed_lights(app.getSettings().getLedLights());
        localuser.setLocale(app.getSettings().getLocale());
        localuser.setNotice_before_event_time(app.getSettings().getNotificationBeforeEventTime());
        localuser.setNotification_sound(app.getSettings().getNotificationSound());
        localuser.setRecommend_festival(app.getSettings().getRecommendFestival());
        localuser.setRecommend_festival_event(app.getSettings().getRecommendFestivalEvent());
        localuser.setRecommend_festival_event_event(app.getSettings().getRecommendFestivalEventEvent());
        localuser.setRecommend_friend(app.getSettings().getRecommendFriend());
        localuser.setSend_crash_reports(app.getSettings().getSendCrashReports());
        localuser.setVibration(app.getSettings().getVibration());

        webServerService.sendSettings(localuser, new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser settingsData, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        }else {
            sendSettingsdata();
            super.onBackPressed();
        }
    }

    private Collection<CurrentUser> getUser() {
        try {
            return db.getDatabaseHelper().getUserDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<CurrentUser>();
    }

    private void call_camera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i = Intent.createChooser(i, "Choose a file");

        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            if(requestCode == CAMERA_REQUEST) {
                path = getPath(SettingsActivity.this, data.getData());
                if(path==null){
                    path = data.getData().getPath();
                    path = path.replace("document", "storage");
                    path = path.replace(":", "/");
                }
            }
        }

        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        twitter_connect.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){
            //filedelete(path);
        }

        if(resultCode==-1)
        {
           path = getPath(SettingsActivity.this, data.getData());
            if(path==null){
                path = data.getData().getPath();
                path = path.replace("document", "storage");
                path = path.replace(":", "/");
            }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @Background
    public void sendImageToServer(){
        File image = new File(path);
        Uri uri = Uri.parse(path);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap b = BitmapFactory.decodeFile(image.getAbsolutePath(),option);
        Matrix matrix = new Matrix();
        matrix.postRotate(getCameraPhotoOrientation(SettingsActivity.this,uri,path));
        Bitmap rotatedBitmap = Bitmap.createBitmap(b , 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        Bitmap d =  scaleDown(rotatedBitmap,3000,true);
        File filesDir = this.getFilesDir();
        File imageFile = new File(filesDir, "profile_pic.jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            d.compress(Bitmap.CompressFormat.JPEG, 0, os);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            d.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            //byte[] byteArray = byteArrayOutputStream .toByteArray();
            //String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        TypedFile in = new TypedFile("image/jpeg", imageFile);
        webServerService.fileUpload(in, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    Object asd = data.get("message");
                    if(asd instanceof JSONObject) {
                        JSONObject message = new JSONObject(data.getString("message"));
                        localuser.setProfileImage(message.getString("profile_image"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                refresDB();
                refresprofpic();
                path="";
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    @Background
    void refresDB(){
        try {
            db.getDatabaseHelper().getUserDataDao().update(localuser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void refresprofpic(){
        if(localuser.getProfileImage()!=null) {
            Picasso.with(SettingsActivity.this).load(localuser.getProfileImage().startsWith("http") ? localuser.getProfileImage() : "http://" + localuser.getProfileImage()).into(profile_image);
            mNavigationDrawerFragment.refresProfilePic(localuser.getProfileImage());
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
