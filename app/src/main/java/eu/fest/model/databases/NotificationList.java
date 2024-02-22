package eu.fest.model.databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.fest.model.Model;

@DatabaseTable(tableName = "NotificationList")
public class NotificationList extends Model {

    @DatabaseField(generatedId = true)
    private int baseid;

    @DatabaseField
    private int festival_event_event_id;

    @DatabaseField
    private int festival_event_id;

    @DatabaseField
    private int festival_id;

    @DatabaseField
    private String event_event_description;

    @DatabaseField
    private String start_date;

    @DatabaseField
    private int festival_event_location_id;

    @DatabaseField
    private String location_name;

    @DatabaseField
    private String performer_ids;

    @DatabaseField
    private String performer_names;

    public NotificationList(){}

    public int getBaseid() {
        return baseid;
    }

    public void setBaseid(int baseid) {
        this.baseid = baseid;
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

    public int getFestival_event_location_id() {
        return festival_event_location_id;
    }

    public void setFestival_event_location_id(int festival_event_location_id) {
        this.festival_event_location_id = festival_event_location_id;
    }

    public int getFestival_id() {
        return festival_id;
    }

    public void setFestival_id(int festival_id) {
        this.festival_id = festival_id;
    }

    public String getEvent_event_description() {
        return event_event_description;
    }

    public void setEvent_event_description(String event_event_description) {
        this.event_event_description = event_event_description;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getPerformer_ids() {
        return performer_ids;
    }

    public void setPerformer_ids(String performer_ids) {
        this.performer_ids = performer_ids;
    }

    public String getPerformer_names() {
        return performer_names;
    }

    public void setPerformer_names(String performer_names) {
        this.performer_names = performer_names;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
}
