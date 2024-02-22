package eu.fest;

import android.app.Application;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import eu.fest.bus.ServiceBus;
import eu.fest.net.FestivalSender;
import eu.fest.service.PersistentCookieStore;
import eu.fest.service.WebServerService;
import eu.fest.utils.Settings;

@ReportsCrashes(
        mode = ReportingInteractionMode.SILENT)

@EApplication
public class ServiceBusApplication extends Application {

    //public static final String WEB_ENDPOINT_ADDRESS = "https://31.14.136.180/";
    //public static final String WEB_ENDPOINT_ADDRESS = "http://backend.festival4ever.com/";
    public static final String WEB_ENDPOINT_ADDRESS = "https://backend.festival4ever.com/";
    //public static final String WEB_ENDPOINT_ADDRESS = "https://192.168.0.100/";

    private Settings settings;

    CookieManager cookieManager;

    @Bean
    WebServerService webServerService;

    @Override
    public void onCreate() {
        super.onCreate();
        settings = new Settings(this);
        cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        /*if(settings.getSendCrashReports()==1) {
            try {
                ACRA.init(ServiceBusApplication.this);
                FestivalSender sender = new FestivalSender(webServerService);
                ACRA.getErrorReporter().setReportSender(sender);
                ACRA.getErrorReporter().putCustomData("device_key", settings.getRegistrationId());
            } catch (RuntimeException e) {
                Log.e("Acra", "I can't init ACRA", e);
                throw e;
            }
        }*/
    }


    @AfterInject
    void setupEventBus() {
        ServiceBus.initialize();
    }

    public String getRegistrationId() {
        return settings.getRegistrationId();
    }

    public void setRegistrationId(String id) {
        settings.setRegistrationId(id);
    }

    public Settings getSettings() {
        return settings;
    }

    public String getRegistrationDevice(){return settings.getRegistrationDevice();}

    public void setRegistrationDevice(String device){settings.setRegistrationDevice(device);}
}
