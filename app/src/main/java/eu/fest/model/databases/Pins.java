package eu.fest.model.databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@DatabaseTable(tableName = "Pins")
public class Pins implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int server_id;

    @DatabaseField
    private int event_id;

    @DatabaseField
    private String location_name;

    @DatabaseField
    private String location_description;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private int location_type;

    @DatabaseField
    private int created_user_id;

    @DatabaseField
    private String addedUsersIdString;

    private ArrayList<Integer> addedUsersId;

    public Pins(){}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getCreated_user_id() {
        return created_user_id;
    }

    public void setCreated_user_id(int created_user_id) {
        this.created_user_id = created_user_id;
    }

    public int getLocation_type() {
        return location_type;
    }

    public void setLocation_type(int location_type) {
        this.location_type = location_type;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_description() {
        return location_description;
    }

    public void setLocation_description(String location_description) {
        this.location_description = location_description;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public ArrayList<Integer> getAddedUsersId() {
        return addedUsersId;
    }

    public void setAddedUsersId(ArrayList<Integer> addedUsersId) {
        this.addedUsersId = addedUsersId;
    }

    public String getAddedUsersIdString() {
        return addedUsersIdString;
    }

    public void setAddedUsersIdString(String addedUsersIdString) {
        this.addedUsersIdString = addedUsersIdString;
    }

    /*public Map<String, String> createPin() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("location_name", location_name);
        map.put("location_description", location_description);
        map.put("latitude", latitude+"");
        map.put("longitude", longitude+"");
        map.put("location_type", location_type+"");
        //map.put("user_id", created_user_id+"");
        //map.put("user_id", addedUsersId+"");

        return map;
    }*/

    public Map<String, Object> createPin(){
        Map<String, Object> map = new HashMap<String, Object>();
        //map.put("user_id", addedUsersId);
        map.put("location_name", location_name);
        map.put("location_description", location_description);
        map.put("latitude", latitude+"");
        map.put("longitude", longitude+"");
        map.put("location_type", location_type+"");
        return map;
    }
}
