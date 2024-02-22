package eu.fest.net.gcm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

import eu.fest.Event.PushNotificationReceivedEvent;
import eu.fest.FestivalListActivity_;
import eu.fest.MainActivity_;
import eu.fest.R;
import eu.fest.ServiceBusApplication;
import eu.fest.UserProfileActivity_;
import eu.fest.bus.ServiceBus;
import eu.fest.model.PushNotification;
import eu.fest.model.User;
import eu.fest.service.WebServerService;

@EBean
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCM";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    User localuser;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        app = (ServiceBusApplication) getApplication();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        String asd = extras.getString("data");

        if (!extras.isEmpty() && app.getSettings().getGlobalNotification() == 1) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                PushNotification push = parsePush(extras);
                if (push.getMessageType().equals(PushNotification.TYPE_BASE_MESSAGE)) {
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_COMMERCIAL)){
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_FRIEND_REQUEST) && app.getSettings().getFriendRequest()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_ACCEPT_FRIEND_REQUEST) && app.getSettings().getAcceptFriendRequest()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FRIEND) && app.getSettings().getRecommendFriend()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_FRIEND_GOING_FESTIVAL_EVENT) && app.getSettings().getFriendGoingFestivalEvent()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_ADD_LOCATION) && app.getSettings().getAddLocation()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL) && app.getSettings().getRecommendFestival()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL_EVENT) && app.getSettings().getRecommendFestivalEvent()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL_EVENT_EVENT) && app.getSettings().getRecommendFestivalEventEvent()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_CHANGE_FESTIVAL_EVENT) && app.getSettings().getChangeFestivalEvent()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }else if(push.getMessageType().equals(PushNotification.TYPE_CHANGE_FESTIVAL_EVENT_EVENT) && app.getSettings().getChangeFestivalEventEvent()==1){
                    ServiceBus.triggerEvent(new PushNotificationReceivedEvent(push));
                    showPush(extras, getResources().getText(R.string.app_name).toString(), push);
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showPush(Bundle extras, String title, PushNotification push) {
        if (app.getSettings().getGlobalNotification() == 0) {
            return;
        }
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("Push üzenet", extras.toString());

        /*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

        Class<?> clazz = null;

        try{
            clazz = Class.forName(taskInfo.get(0).topActivity.getClassName());
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }*/
        Intent intent;
        if(push.getMessageType().equals(PushNotification.TYPE_BASE_MESSAGE)){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtras(extras);
        }else if(push.getMessageType().equals(PushNotification.TYPE_COMMERCIAL)){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtras(extras);
        }else if(push.getMessageType().equals(PushNotification.TYPE_FRIEND_REQUEST) && app.getSettings().getFriendRequest()==1){
            intent = new Intent(this, UserProfileActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user_id",push.getUser_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_ACCEPT_FRIEND_REQUEST) && app.getSettings().getAcceptFriendRequest()==1){
            intent = new Intent(this, UserProfileActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user_id",push.getUser_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FRIEND) && app.getSettings().getRecommendFriend()==1){
            intent = new Intent(this, UserProfileActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user_id",push.getUser_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_FRIEND_GOING_FESTIVAL_EVENT) && app.getSettings().getFriendGoingFestivalEvent()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user_id",push.getUser_id());
            intent.putExtra("festival_event_id",push.getFestival_event_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_ADD_LOCATION) && app.getSettings().getAddLocation()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_event_id",push.getFestival_event_id());
            intent.putExtra("location_id",push.getLocation_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL) && app.getSettings().getRecommendFestival()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_id",push.getFestival_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL_EVENT) && app.getSettings().getRecommendFestivalEvent()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_event_id",push.getFestival_event_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_RECOMMEND_FESTIVAL_EVENT_EVENT) && app.getSettings().getRecommendFestivalEventEvent()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_event_id",push.getFestival_event_id());
            intent.putExtra("festival_event_event_id",push.getFestival_event_event_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_CHANGE_FESTIVAL_EVENT) && app.getSettings().getChangeFestivalEvent()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_event_id",push.getFestival_event_id());
        }else if(push.getMessageType().equals(PushNotification.TYPE_CHANGE_FESTIVAL_EVENT_EVENT) && app.getSettings().getChangeFestivalEventEvent()==1){
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("festival_event_id",push.getFestival_event_id());
            intent.putExtra("festival_event_event_id",push.getFestival_event_event_id());
        }else{
            intent = new Intent(this, FestivalListActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtras(extras);
        }

        /*if(clazz != null){
            intent = new Intent(this, clazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtras(extras);
        }
        else{

        }*/

        // Starts the activity on notification click
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(push.getMessage()))
                .setContentText(push.getMessage());
        if(app.getSettings().getVibration()==1) {
            mBuilder.setVibrate(new long[]{0, 1000, 1000});
        }
        mBuilder.setContentIntent(contentIntent);

        if(app.getSettings().getNotificationSound()==1) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = 0xff7208c2;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private PushNotification parsePush(Bundle extras) {
        String msg = extras.getString("data");
        ObjectMapper mapper = new ObjectMapper();
        Log.d("Push", extras.getString("user_id") +" , "+extras.getString("festival_event_id")+" , "+extras.getString("festival_event_id")+" , "+extras.getString("festival_event_id"));
        PushNotification push = null;
        //PushNotification(String messageType, String message, int user_id, int festival_id, int festival_event_id, int festival_event_event_id, int location_id)
        if(extras.getString("type").equals("friend_request")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),Integer.parseInt(extras.getString("user_id")),0,0,0,0);
        }else if (extras.getString("type").equals("accept_friend_request")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),Integer.parseInt(extras.getString("user_id")),0,0,0,0);
        }else if (extras.getString("type").equals("recommend_friend")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),Integer.parseInt(extras.getString("user_id")),0,0,0,0);
        }else if (extras.getString("type").equals("friend_going_festival_event")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),Integer.parseInt(extras.getString("user_id")),0,Integer.parseInt(extras.getString("festival_event_id")),0,0);
        }else if (extras.getString("type").equals("add_location")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),0,Integer.parseInt(extras.getString("location_id")));
        }else if (extras.getString("type").equals("recommend_festival")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),0,0);
        }else if (extras.getString("type").equals("recommend_festival_event")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),0,0);
        }else if (extras.getString("type").equals("recommend_festival_event_event")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),Integer.parseInt(extras.getString("festival_event_event_id")),0);
        }else if (extras.getString("type").equals("changed_festival_event")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),0,0);
        }else if (extras.getString("type").equals("changed_festival_event_event")){
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"),0,0,Integer.parseInt(extras.getString("festival_event_id")),Integer.parseInt(extras.getString("festival_event_event_id")),0);
        }else{
            push =  new PushNotification(extras.getString("type"),extras.getString("gcm.notification.body"));
        }

        return push;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_logo_white : R.drawable.app_logo;
    }
}
