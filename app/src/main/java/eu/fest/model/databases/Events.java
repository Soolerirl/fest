package eu.fest.model.databases;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@DatabaseTable(tableName = "Events")
public class Events implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int serverId;

    @DatabaseField
    private int festivalId;

    @DatabaseField
    private String start_date;

    @DatabaseField
    private String end_date;

    @DatabaseField
    private String location_country;

    @DatabaseField
    private String location_city;

    @DatabaseField
    private String festival_location;

    @DatabaseField
    private String festival_event_description;

    @DatabaseField
    private String festival_event_logo;

    @DatabaseField
    private String festival_event_name;

    @DatabaseField
    private int going_festival_event;

    @DatabaseField
    private int watch_festival_event;

    @DatabaseField
    private double user_rating_festival_event;

    @DatabaseField
    private double rating_festival_event;

    @DatabaseField
    private boolean is_going_festival_event;

    @DatabaseField
    private boolean is_watch_festival_event;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private String goingfriends;

    public Events(){}

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getGoing_festival_event() {
        return going_festival_event;
    }

    public void setGoing_festival_event(int going_festival_event) {
        this.going_festival_event = going_festival_event;
    }

    public int getWatch_festival_event() {
        return watch_festival_event;
    }

    public void setWatch_festival_event(int watch_festival_event) {
        this.watch_festival_event = watch_festival_event;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getFestival_event_description() {
        return festival_event_description;
    }

    public void setFestival_event_description(String festival_event_description) {
        this.festival_event_description = festival_event_description;
    }

    public String getFestival_location() {
        return festival_location;
    }

    public void setFestival_location(String festival_location) {
        this.festival_location = festival_location;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) {
        this.location_country = location_country;
    }

    public int getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(int festivalId) {
        this.festivalId = festivalId;
    }

    public String getFestival_event_logo() {
        return festival_event_logo;
    }

    public void setFestival_event_logo(String festival_event_logo) {
        this.festival_event_logo = festival_event_logo;
    }

    public void setFestival_event_name(String festival_event_name) {
        this.festival_event_name = festival_event_name;
    }

    public String getFestival_event_name() {
        return festival_event_name;
    }

    public double getRating_festival_event() {
        return rating_festival_event;
    }

    public void setRating_festival_event(double rating_festival_event) {
        this.rating_festival_event = rating_festival_event;
    }

    public double getUser_rating_festival_event() {
        return user_rating_festival_event;
    }

    public void setUser_rating_festival_event(double user_rating_festival_event) {
        this.user_rating_festival_event = user_rating_festival_event;
    }

    public boolean is_going_festival_event() {
        return is_going_festival_event;
    }

    public void setIs_going_festival_event(boolean is_going_festival_event) {
        this.is_going_festival_event = is_going_festival_event;
    }

    public boolean is_watch_festival_event() {
        return is_watch_festival_event;
    }

    public void setIs_watch_festival_event(boolean is_watch_festival_event) {
        this.is_watch_festival_event = is_watch_festival_event;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGoingfriends() {
        return goingfriends;
    }

    public void setGoingfriends(String goingfriends) {
        this.goingfriends = goingfriends;
    }

    public Map<String, String> mapForRating() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("rating",rating_festival_event+"");

        return map;
    }
}
