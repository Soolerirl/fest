package eu.fest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import eu.fest.FestivalListActivity;
import eu.fest.FestivalListActivity_;
import eu.fest.R;
import eu.fest.StartActivity;

public class AlarmReceiver extends BroadcastReceiver {

    String performer;

    String hour;

    String minute;

    @Override
    public void onReceive(Context context, Intent intent) {
        performer = intent.getExtras().getString("performer");
        hour = intent.getExtras().getString("hour");
        minute = intent.getExtras().getString("minute");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getResources().getString(R.string.notif_time_text), performer, Integer.parseInt(hour), Integer.parseInt(minute))))
                .setContentText(String.format(context.getResources().getString(R.string.notif_time_text), performer, Integer.parseInt(hour), Integer.parseInt(minute)));

        mBuilder.setVibrate(new long[]{0, 1000, 1000});

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = 0xff7208c2;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;

        Intent resultIntent = new Intent(context, FestivalListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(FestivalListActivity_.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_logo_white : R.drawable.app_logo;
    }
}
