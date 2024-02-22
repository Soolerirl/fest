package eu.fest;

import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.EditText;

public class BaseActionBarActivity extends ActionBarActivity {
    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;

        } else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }

    public final static boolean isEmailValid(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String[] convertStringToArray(String str, String separator){
        String[] arr = str.split(separator);
        return arr;
    }

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
    }
}
