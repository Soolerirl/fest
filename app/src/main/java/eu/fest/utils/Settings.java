package eu.fest.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class Settings {
    private static final String REGISTRATION_ID_KEY = "registration_id";
    private static final String REGISTRATION_DEVICE = "registration_device";
    private static final String ACCEPT_FRIEND_REQUEST = "accept_friend_request";
    private static final String ADD_LOCATION = "add_location";
    private static final String CHANGE_FESTIVAL_EVENT = "changed_festival_event";
    private static final String CHANGE_FESTIVAL_EVENT_EVENT = "changed_festival_event_event";
    private static final String FACEBOOK = "facebook";
    private static final String FRIEND_REQUEST = "friend_request";
    private static final String FRIEND_GOING_FESTIVAL_EVENT = "friend_going_festival_event";
    private static final String GLOBAL_NOTIFICATION = "global_crash_reports";
    private static final String HAVEPASSWORD = "have_password";
    private static final String LANGUAGE = "language";
    private static final String LED_LIGHTS = "led_lights";
    private static final String LOCALE = "locale";
    private static final String NOTIFICATION_SOUND = "notification_sound";
    private static final String NOTIFICATION_BEFORE_EVENT_TIME = "notice_before_event_time";
    private static final String REMEMBERME = "remember_me";
    private static final String RECOMMEND_FRIEND = "recommend_friend";
    private static final String RECOMMEND_FESTIVAL = "recommend_festival";
    private static final String RECOMMEND_FESTIVAL_EVENT = "recommend_festival_event";
    private static final String RECOMMEND_FESTIVAL_EVENT_EVENT = "recommend_festival_event_event";
    private static final String SEND_CRASH_REPORTS = "send_crash_reports";
    private static final String TWITTER = "twitter";
    private static final String VIBRATION = "vibration";

    private final SharedPreferences settings;

    public Settings(Context act) {
        settings = act.getSharedPreferences("service_bus", Context.MODE_PRIVATE);
    }

    public void setRegistrationId(String id) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(REGISTRATION_ID_KEY, id);
        editor.commit();
    }

    public String getRegistrationId() {
        return settings.getString(REGISTRATION_ID_KEY, "");
    }

    public void setRegistrationDevice(String registrationDevice) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(REGISTRATION_DEVICE, registrationDevice);
        editor.commit();
    }

    public String getRegistrationDevice() {
        return settings.getString(REGISTRATION_DEVICE, "");
    }

    public void setLocale(String locale) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LOCALE, locale);
        editor.commit();
    }

    public String getLocale() {
        return settings.getString(LOCALE, "Hungary");
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LANGUAGE, language);
        editor.commit();
    }

    public String getLanguage() {
        return settings.getString(LANGUAGE, "Hungarian");
    }

    public void setRememberme(boolean enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(REMEMBERME, enabled);
        editor.commit();
    }

    public boolean getRememberme(){
        return settings.getBoolean(REMEMBERME, false);
    }


    public void setHavepassword(boolean enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(HAVEPASSWORD, enabled);
        editor.commit();
    }

    public boolean getHavepassword(){
        return settings.getBoolean(HAVEPASSWORD, false);
    }

    public void setFacebook(boolean enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(FACEBOOK, enabled);
        editor.commit();
    }

    public boolean getFacebook(){
        return settings.getBoolean(FACEBOOK, false);
    }

    public void setTwitter(boolean enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TWITTER, enabled);
        editor.commit();
    }

    public boolean getTwitter(){
        return settings.getBoolean(TWITTER, false);
    }

    public void setAcceptFriendRequest(int enabled) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ACCEPT_FRIEND_REQUEST, enabled);
        editor.commit();
    }

    public int getAcceptFriendRequest(){
        return settings.getInt(ACCEPT_FRIEND_REQUEST, 1);
    }

    public void setAddLocation(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ADD_LOCATION, enabled);
        editor.commit();
    }

    public int getAddLocation(){
        return settings.getInt(ADD_LOCATION, 1);
    }

    public void setChangeFestivalEvent(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CHANGE_FESTIVAL_EVENT, enabled);
        editor.commit();
    }

    public int getChangeFestivalEvent(){
        return settings.getInt(CHANGE_FESTIVAL_EVENT, 1);
    }

    public void setChangeFestivalEventEvent(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CHANGE_FESTIVAL_EVENT_EVENT, enabled);
        editor.commit();
    }

    public int getChangeFestivalEventEvent(){
        return settings.getInt(CHANGE_FESTIVAL_EVENT_EVENT, 1);
    }

    public void setFriendRequest(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FRIEND_REQUEST, enabled);
        editor.commit();
    }

    public int getFriendRequest(){
        return settings.getInt(FRIEND_REQUEST, 1);
    }

    public void setFriendGoingFestivalEvent(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FRIEND_GOING_FESTIVAL_EVENT, enabled);
        editor.commit();
    }

    public int getFriendGoingFestivalEvent(){
        return settings.getInt(FRIEND_GOING_FESTIVAL_EVENT, 1);
    }

    public void setGlobalNotification(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(GLOBAL_NOTIFICATION, enabled);
        editor.commit();
    }

    public int getGlobalNotification(){
        return settings.getInt(GLOBAL_NOTIFICATION, 1);
    }

    public void setLedLights(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LED_LIGHTS, enabled);
        editor.commit();
    }

    public int getLedLights(){
        return settings.getInt(LED_LIGHTS, 1);
    }

    public void setNotificationSound(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(NOTIFICATION_SOUND, enabled);
        editor.commit();
    }

    public int getNotificationSound(){
        return settings.getInt(NOTIFICATION_SOUND, 1);
    }

    public void setNotificationBeforeEventTime(String enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(NOTIFICATION_BEFORE_EVENT_TIME, enabled);
        editor.commit();
    }

    public String getNotificationBeforeEventTime(){
        return settings.getString(NOTIFICATION_BEFORE_EVENT_TIME, "01:00:00");
    }

    public void setRecommendFriend(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RECOMMEND_FRIEND, enabled);
        editor.commit();
    }

    public int getRecommendFriend(){
        return settings.getInt(RECOMMEND_FRIEND, 1);
    }

    public void setRecommendFestival(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RECOMMEND_FESTIVAL, enabled);
        editor.commit();
    }

    public int getRecommendFestival(){
        return settings.getInt(RECOMMEND_FESTIVAL, 1);
    }

    public void setRecommendFestivalEvent(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RECOMMEND_FESTIVAL_EVENT, enabled);
        editor.commit();
    }

    public int getRecommendFestivalEvent(){
        return settings.getInt(RECOMMEND_FESTIVAL_EVENT, 1);
    }

    public void setRecommendFestivalEventEvent(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RECOMMEND_FESTIVAL_EVENT_EVENT, enabled);
        editor.commit();
    }

    public int getRecommendFestivalEventEvent(){
        return settings.getInt(RECOMMEND_FESTIVAL_EVENT_EVENT, 1);
    }

    public void setSendCrashReports(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SEND_CRASH_REPORTS, enabled);
        editor.commit();
    }

    public int getSendCrashReports(){
        return settings.getInt(SEND_CRASH_REPORTS, 1);
    }

    public void setVibration(int enabled){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(VIBRATION, enabled);
        editor.commit();
    }

    public int getVibration(){
        return settings.getInt(VIBRATION, 1);
    }
}
