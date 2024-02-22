package eu.fest.model.databases;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;

import eu.fest.model.Model;

@DatabaseTable(tableName = "CurrentUser")
public class CurrentUser extends Model {

    @DatabaseField(generatedId = true)
    private int id_key;

    @SerializedName("user_id")
    @DatabaseField
    private int user_id;

    @SerializedName("first_name")
    @DatabaseField
    private String firstName;

    @SerializedName("last_name")
    @DatabaseField
    private String lastName;

    @SerializedName("profile_image")
    @DatabaseField
    private String profileImage;

    @SerializedName("gender")
    @DatabaseField
    private String gender;

    @SerializedName("birthday")
    @DatabaseField
    private String birthday;

    @SerializedName("email")
    @DatabaseField
    private String email;

    @SerializedName("locale")
    private String locale;

    @SerializedName("language")
    private String language;

    @SerializedName("notice_before_event_time")
    private String notice_before_event_time;

    @SerializedName("global_notifications")
    private int global_notifications;

    @SerializedName("friend_request")
    private int friend_request;

    @SerializedName("accept_friend_request")
    private int accept_friend_request;

    @SerializedName("recommend_friend")
    private int recommend_friend;

    @SerializedName("friend_going_festival_event")
    private int friend_going_festival_event;

    @SerializedName("add_location")
    private int add_location;

    @SerializedName("recommend_festival")
    private int recommend_festival;

    @SerializedName("recommend_festival_event")
    private int recommend_festival_event;

    @SerializedName("recommend_festival_event_event")
    private int recommend_festival_event_event;

    @SerializedName("changed_festival_event")
    private int changed_festival_event;

    @SerializedName("changed_festival_event_event")
    private int changed_festival_event_event;

    @SerializedName("send_crash_reports")
    private int send_crash_reports;

    @SerializedName("vibration")
    private int vibration;

    @SerializedName("notification_sound")
    private int notification_sound;

    @SerializedName("led_lights")
    private int led_lights;

    @SerializedName("have_password")
    private boolean have_password;

    @SerializedName("facebook")
    private boolean facebook;

    @SerializedName("twitter")
    private boolean twitter;

    @SerializedName("success")
    private boolean success;

    public CurrentUser(){}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isFacebook() {
        return facebook;
    }

    public void setFacebook(boolean facebook) {
        this.facebook = facebook;
    }

    public boolean isHave_password() {
        return have_password;
    }

    public void setHave_password(boolean have_password) {
        this.have_password = have_password;
    }

    public boolean isTwitter() {
        return twitter;
    }

    public void setTwitter(boolean twitter) {
        this.twitter = twitter;
    }

    public int getAccept_friend_request() {
        return accept_friend_request;
    }

    public void setAccept_friend_request(int accept_friend_request) {
        this.accept_friend_request = accept_friend_request;
    }

    public int getAdd_location() {
        return add_location;
    }

    public void setAdd_location(int add_location) {
        this.add_location = add_location;
    }

    public int getChanged_festival_event() {
        return changed_festival_event;
    }

    public void setChanged_festival_event(int changed_festival_event) {
        this.changed_festival_event = changed_festival_event;
    }

    public int getChanged_festival_event_event() {
        return changed_festival_event_event;
    }

    public void setChanged_festival_event_event(int changed_festival_event_event) {
        this.changed_festival_event_event = changed_festival_event_event;
    }

    public int getFriend_going_festival_event() {
        return friend_going_festival_event;
    }

    public void setFriend_going_festival_event(int friend_going_festival_event) {
        this.friend_going_festival_event = friend_going_festival_event;
    }

    public int getFriend_request() {
        return friend_request;
    }

    public void setFriend_request(int friend_request) {
        this.friend_request = friend_request;
    }

    public int getGlobal_notifications() {
        return global_notifications;
    }

    public void setGlobal_notifications(int global_notifications) {
        this.global_notifications = global_notifications;
    }

    public int getLed_lights() {
        return led_lights;
    }

    public void setLed_lights(int led_lights) {
        this.led_lights = led_lights;
    }

    public int getNotification_sound() {
        return notification_sound;
    }

    public void setNotification_sound(int notification_sound) {
        this.notification_sound = notification_sound;
    }

    public String getNotice_before_event_time() {
        return notice_before_event_time;
    }

    public void setNotice_before_event_time(String notice_before_event_time) {
        this.notice_before_event_time = notice_before_event_time;
    }

    public int getRecommend_festival() {
        return recommend_festival;
    }

    public void setRecommend_festival(int recommend_festival) {
        this.recommend_festival = recommend_festival;
    }

    public int getRecommend_festival_event() {
        return recommend_festival_event;
    }

    public void setRecommend_festival_event(int recommend_festival_event) {
        this.recommend_festival_event = recommend_festival_event;
    }

    public int getRecommend_festival_event_event() {
        return recommend_festival_event_event;
    }

    public void setRecommend_festival_event_event(int recommend_festival_event_event) {
        this.recommend_festival_event_event = recommend_festival_event_event;
    }

    public int getRecommend_friend() {
        return recommend_friend;
    }

    public void setRecommend_friend(int recommend_friend) {
        this.recommend_friend = recommend_friend;
    }

    public int getVibration() {
        return vibration;
    }

    public void setVibration(int vibration) {
        this.vibration = vibration;
    }

    public int getSend_crash_reports() {
        return send_crash_reports;
    }

    public void setSend_crash_reports(int send_crash_reports) {
        this.send_crash_reports = send_crash_reports;
    }

    public Map<String, String> mapforSettings() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("locale", locale);
        map.put("language", language);
        map.put("send_crash_reports", send_crash_reports+"");
        map.put("vibration", vibration+"");
        map.put("notification_sound", notification_sound+"");
        map.put("led_lights", led_lights+"");
        map.put("notice_before_event_time", notice_before_event_time);
        map.put("global_notifications", global_notifications+"");
        map.put("friend_request", friend_request+"");
        map.put("accept_friend_request", accept_friend_request+"");
        map.put("recommend_friend", recommend_friend+"");
        map.put("friend_going_festival_event", friend_going_festival_event+"");
        map.put("add_location", add_location+"");
        map.put("recommend_festival", recommend_festival+"");
        map.put("recommend_festival_event", recommend_festival_event+"");
        map.put("recommend_festival_event_event", recommend_festival_event_event+"");
        map.put("changed_festival_event", changed_festival_event+"");
        map.put("changed_festival_event_event", changed_festival_event_event+"");

        return map;
    }

    public Map<String, String> mapForChangeData() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("first_name",firstName);
        map.put("last_name",lastName);
        map.put("gender",gender);
        map.put("birthdate",birthday);

        return map;
    }
}
