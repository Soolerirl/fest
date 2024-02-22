package eu.fest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.fest.model.User;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_email_change)
public class ChangeEmailActivity extends BaseActivity implements Validator.ValidationListener {

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @ViewById
    EditText emailInput;

    private User localuser;

    private Validator validator;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);

        this.localuser = new User();
    }

    @Click(R.id.email_change_btn)
    void emailClickHandle() {
        validator.validate();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Email csere/csatolás");
        mDialog.setCancelable(true);
        mDialog.show();
    }

    @UiThread
    void save() {
        if(isFilled(emailInput)) {
            if (isEmailValid(lastEmptyCharDelete(emailInput))) {
                localuser.setEmail(lastEmptyCharDelete(emailInput));
            } else {
                mDialog.setMessage("Valid email formátumot adjon meg");
                return;
            }
        }else{
            mDialog.setMessage("Email megadása kötelező");
            return;
        }

        webServerService.changeEmail(localuser, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                if (user.isValid()) {
                    mDialog.dismiss();
                    SettingsActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                } else {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });

    }

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
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

    String lastEmptyCharDelete(EditText editText){
        String str = editText.getText().toString();
        if (str.charAt(str.length()-1)==' ')
        {
            str = str.replace(str.substring(str.length()-1), "");
        }
        return str;
    }
}
