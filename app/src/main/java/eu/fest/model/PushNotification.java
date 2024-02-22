package eu.fest.model;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonProperty;

public class PushNotification extends Model{


    public static final String TYPE_BASE_MESSAGE = "message";
    public static final String TYPE_COMMERCIAL = "commercial";
    public static final String TYPE_FRIEND_REQUEST = "friend_request";
    public static final String TYPE_ACCEPT_FRIEND_REQUEST = "accept_friend_request";
    public static final String TYPE_RECOMMEND_FRIEND = "recommend_friend";
    public static final String TYPE_FRIEND_GOING_FESTIVAL_EVENT = "friend_going_festival_event";
    public static final String TYPE_ADD_LOCATION = "add_location";
    public static final String TYPE_RECOMMEND_FESTIVAL = "recommend_festival";
    public static final String TYPE_RECOMMEND_FESTIVAL_EVENT = "recommend_festival_event";
    public static final String TYPE_RECOMMEND_FESTIVAL_EVENT_EVENT = "recommend_festival_event_event";
    public static final String TYPE_CHANGE_FESTIVAL_EVENT = "changed_festival_event";
    public static final String TYPE_CHANGE_FESTIVAL_EVENT_EVENT = "changed_festival_event_event";

    private int id;

    @JsonProperty("type")
    private String messageType;

    @JsonProperty("user_id")
    private int user_id;

    @JsonProperty("festival_event_id")
    private int festival_event_id;

    @JsonProperty("festival_event_event_id")
    private int festival_event_event_id;

    @JsonProperty("location_id")
    private int location_id;

    @JsonProperty("festival_id")
    private int festival_id;

    private String message;

    private String data;

    private String image;

    private int objectid;

    private Date timestamp;

    public PushNotification() {
    }


    public PushNotification(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public PushNotification(String messageType, String message, int user_id, int festival_id, int festival_event_id, int festival_event_event_id, int location_id) {
        this.messageType = messageType;
        this.message = message;
        this.user_id = user_id;
        this.festival_id = festival_id;
        this.festival_event_id = festival_event_id;
        this.festival_event_event_id = festival_event_event_id;
        this.location_id = location_id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectid() {
        return objectid;
    }

    public void setObjectid(int objectid) {
        this.objectid = objectid;
    }

    public int getFestival_event_event_id() {
        return festival_event_event_id;
    }

    public void setFestival_event_event_id(int festival_event_event_id) {
        this.festival_event_event_id = festival_event_event_id;
    }

    public int getFestival_event_id() {
        return festival_event_id;
    }

    public void setFestival_event_id(int festival_event_id) {
        this.festival_event_id = festival_event_id;
    }

    public int getFestival_id() {
        return festival_id;
    }

    public void setFestival_id(int festival_id) {
        this.festival_id = festival_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

