package eu.fest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import eu.fest.model.User;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_password_change)
public class PasswordChangeActivity extends BaseActivity implements Validator.ValidationListener{

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    private Validator validator;

    @ViewById
    EditText passwordInput;

    @ViewById
    EditText passwordagainInput;

    @ViewById
    EditText oldpasswordInput;

    @Extra("user")
    static
    User localuser;

    private ProgressDialog mDialog;

    @AfterViews
    void setupUi() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        localuser = new User();
    }

    @Click(R.id.newpasswordbtn)
    void newpwClickHandler(){
        mDialog = new ProgressDialog(PasswordChangeActivity.this);
        mDialog.setMessage("Pwchange");
        mDialog.setCancelable(true);
        mDialog.show();
        validator.validate();
    }

    @UiThread
    void send() {
        if(isFilled(oldpasswordInput)) {
            if (oldpasswordInput.getText().toString().length()>=8) {
                localuser.setOldpassword(lastEmptyCharDelete(oldpasswordInput));
            } else {
                mDialog.setMessage("Régi jelszó kevesebb mint 8 karakter");
                return;
            }
        } else {
            mDialog.setMessage("Régi jelszó megadása kötelező");
            return;
        }

        if(passwordInput.getText().toString().length()>=8) {
            if (isFilled(passwordInput) && isFilled(passwordagainInput)) {
                if (lastEmptyCharDelete(passwordInput).equals(lastEmptyCharDelete(passwordagainInput))) {
                    localuser.setPassword(lastEmptyCharDelete(passwordInput));
                    localuser.setPasswordAgain(lastEmptyCharDelete(passwordagainInput));
                } else {
                    mDialog.setMessage("A két jelszó nem eggyezik");
                    return;
                }
            } else {
                mDialog.setMessage("Minkét mező megadása kötelező");
                return;
            }
        } else {
            mDialog.setMessage("Új jelszó kevesebb mint 8 karakter");
            return;
        }

        localuser.setDeviceId(app.getRegistrationId());

        webServerService.changePassword(localuser, new Callback<User>() {
            @Override
            public void success(final User user, Response response) {
                if (user.isSuccess()) {
                    webServerService.logout(localuser, new Callback<User>() {
                        @Override
                        public void success(User user, Response response) {
                            mDialog.dismiss();
                            app.getSettings().setRememberme(false);
                            LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            mDialog.setMessage(error.getMessage());
                        }
                    });
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
