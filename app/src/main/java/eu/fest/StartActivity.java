package eu.fest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import java.util.Locale;

@EActivity(R.layout.activity_start)
public class StartActivity extends BaseActivity {

    @App
    ServiceBusApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int DELAY = 5000;

        Locale myLocale = new Locale(app.getSettings().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(app.getSettings().getRememberme())
                {
                    //MainActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
                    SyncActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
                }else {
                    LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
                }
            }
        }, 0);
    }
}
