package eu.fest.model.databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

@DatabaseTable(tableName = "Performers")
public class Performers implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int serverId;

    @DatabaseField
    private int festivalId;

    @DatabaseField
    private int eventId;

    @DatabaseField
    private int performer_id;

    @DatabaseField
    private String start_date;

    @DatabaseField
    private String start_time;

    @DatabaseField
    private String performer_name;

    @DatabaseField
    private String performer_category_name;

    @DatabaseField
    private String performer_logo;

    @DatabaseField
    private String performer_genre;

    @DatabaseField
    private boolean isgoing;

    @DatabaseField
    private String location_name;

    @DatabaseField
    private int festival_event_location_id;

    @DatabaseField
    private int count_going_festival_event_event;

    public Performers(){}

    public int getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(int festivalId) {
        this.festivalId = festivalId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getPerformer_id() {
        return performer_id;
    }

    public void setPerformer_id(int performer_id) {
        this.performer_id = performer_id;
    }

    public String getPerformer_category_name() {
        return performer_category_name;
    }

    public void setPerformer_category_name(String performer_category_name) {
        this.performer_category_name = performer_category_name;
    }

    public String getPerformer_genre() {
        return performer_genre;
    }

    public void setPerformer_genre(String performer_genre) {
        this.performer_genre = performer_genre;
    }

    public String getPerformer_logo() {
        return performer_logo;
    }

    public void setPerformer_logo(String performer_logo) {
        this.performer_logo = performer_logo;
    }

    public String getPerformer_name() {
        return performer_name;
    }

    public void setPerformer_name(String performer_name) {
        this.performer_name = performer_name;
    }

    public String getStart_date() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //return formatter.format(start_date);
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public int getCount_going_festival_event_event() {
        return count_going_festival_event_event;
    }

    public void setCount_going_festival_event_event(int count_going_festival_event_event) {
        this.count_going_festival_event_event = count_going_festival_event_event;
    }

    public int getFestival_event_location_id() {
        return festival_event_location_id;
    }

    public void setFestival_event_location_id(int festival_event_location_id) {
        this.festival_event_location_id = festival_event_location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public boolean isgoing() {
        return isgoing;
    }

    public void setIsgoing(boolean isgoing) {
        this.isgoing = isgoing;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
}
