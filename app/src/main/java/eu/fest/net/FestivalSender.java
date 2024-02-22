package eu.fest.net;

import android.content.Context;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import eu.fest.model.User;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FestivalSender implements ReportSender {

    WebServerService webServerService;

    public FestivalSender(WebServerService webServerService){
        this.webServerService=webServerService;
    }

    private static String BASE_URL = "https://192.168.0.100/api/1/crash-report";

    private String createCrashLog(CrashReportData report) {
        Date now = new Date();
        StringBuilder log = new StringBuilder();

        log.append(report.get(ReportField.LOGCAT));

        /*log.append("Package: " + report.get(ReportField.PACKAGE_NAME) + "\n");
        log.append("Version: " + report.get(ReportField.APP_VERSION_CODE) + "\n");
        log.append("Android: " + report.get(ReportField.ANDROID_VERSION) + "\n");
        log.append("Manufacturer: " + android.os.Build.MANUFACTURER + "\n");
        log.append("Model: " + report.get(ReportField.PHONE_MODEL) + "\n");
        log.append("Date: " + now + "\n");
        log.append("\n");
        log.append(report.get(ReportField.STACK_TRACE));*/

        return log.toString();
    }

    public Map<String, String> mapforLog(String log){
        Map<String, String> map = new HashMap<String, String>();

        map.put("report", log);

        return map;
    }


    /*@Override
    public void send(CrashReportData report) throws ReportSenderException {
        String log = createCrashLog(report);
        webServerService.sendCrash(mapforLog(log), new Callback<User>() {
            @Override
            public void success(User user, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }*/

    @Override
    public void send(Context context, CrashReportData report) throws ReportSenderException {
        String log = createCrashLog(report);
        webServerService.sendCrash(mapforLog(log), new Callback<User>() {
            @Override
            public void success(User user, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
