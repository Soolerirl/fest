package eu.fest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.fest.model.User;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_registration)
public class RegistrationActivity extends BaseActivity implements Validator.ValidationListener{


    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @ViewById
    Spinner genderspiner;

    @ViewById
    EditText emailInput;

    @ViewById
    EditText firstNameInput;

    @ViewById
    EditText lastNameInput;

    @ViewById
    EditText passwordInput;

    @ViewById
    EditText passwordInputagain;

    @ViewById
    EditText birthdateInput;

    @ViewById
    TextView registration_title;

    @ViewById
    CheckBox licence_checkbox;

    private User user;

    private Validator validator;

    private Dialog mDialog;

    TextView text;

    private Calendar calendar;

    private int year, month, day;

    private HashMap<String, String> genders = new HashMap<String, String>();

    @AfterViews
    void setup(){

        validator = new Validator(this);
        validator.setValidationListener(this);

        this.user = new User();
        mDialog = new Dialog(RegistrationActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.message_dialog);
        text = (TextView) mDialog.findViewById(R.id.text);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"chocolatedeluce.ttf");
        registration_title.setTypeface(typeface);

        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        genders.put(" ", " ");
        genders.put(getResources().getString(R.string.male), "male");
        genders.put(getResources().getString(R.string.female), "female");

        for (Map.Entry<String, String> gender: genders.entrySet()) {
            genderAdapter.add(gender.getKey());
        }

        genderspiner.setAdapter(genderAdapter);
        genderspiner.setSelection(0);
        user.setGender(genderAdapter.getItem(0));
        genderspiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gender = genderAdapter.getItem(position);
                user.setGender(gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        setOnFocusChangeListener(emailInput);
        setOnFocusChangeListener(firstNameInput);
        setOnFocusChangeListener(lastNameInput);
        setOnFocusChangeListener(passwordInput);
        setOnFocusChangeListener(passwordInputagain);
    }

    private void showDate(int year, int month, int day) {
        String birthday = year + "." + month + "." + day;
        user.setBirthDate(birthday);
        birthdateInput.setText(birthday);
    }

    @Click(R.id.birthdateInput)
    void clickHandle() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    @Click(R.id.licence)
    void licenceClickHandle(){
        WebViewActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).url("http://backend.festival4ever.com/api/v1/get-aszf").start();
    }

    @Click(R.id.registration)
    void registrationClickHandler() {
        mDialog.setCancelable(true);
        Button dialogButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
        validator.validate();
    }

    @UiThread
    void send() {

        if (isFilled(firstNameInput)) {
            user.setFirstName(lastEmptyCharDelete(firstNameInput));
        }else{
            text.setText("Keresztnév megadása kötelező");
            return;
        }

        if (isFilled(lastNameInput)) {
            user.setLastName(lastEmptyCharDelete(lastNameInput));
        }else{
            text.setText("Vezetéknév megadása kötelező");
            return;
        }

        if (isFilled(emailInput)) {
            if(isEmailValid(lastEmptyCharDelete(emailInput))) {
                user.setEmail(lastEmptyCharDelete(emailInput));
            }else{
                text.setText("Valid email-t adjon meg");
                return;
            }
        }else{
            text.setText("Email megadása kötelező");
            return;
        }

        if(passwordInput.getText().toString().length()>=8) {
            if (isFilled(passwordInput) && isFilled(passwordInputagain)) {
                if (lastEmptyCharDelete(passwordInput).equals(lastEmptyCharDelete(passwordInputagain))) {
                    user.setPassword(lastEmptyCharDelete(passwordInput));
                    user.setPasswordAgain(lastEmptyCharDelete(passwordInputagain));
                } else {
                    text.setText("A két jelszó nem eggyezik");
                    return;
                }
            } else {
                text.setText("Mindkét jelszó megadása kötelező");
                return;
            }
        } else {
            text.setText("Jelszó megadása kötelező");
            return;
        }

        if(licence_checkbox.isChecked()) {
            text.setText("Felhasználási feltételek elfogadása kötelező");
            return;
        }

        webServerService.registration(user, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                if (user.isSuccess()) {
                    mDialog.dismiss();
                    LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
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
    public void onValidationSucceeded() {
        send();
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

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
    }

    String lastEmptyCharDelete(EditText editText){
        String str = editText.getText().toString();
        if (str.charAt(str.length()-1)==' ')
        {
            str = str.replace(str.substring(str.length()-1), "");
        }
        return str;
    }
}
