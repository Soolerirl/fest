package eu.fest.model.databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "Festivals")
public class Festival implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int serverId;

    @DatabaseField
    private String festival_name;

    @DatabaseField
    private String festival_logo;

    @DatabaseField
    private String festival_description;

    @DatabaseField
    private String festival_homepage_url;

    @DatabaseField
    private String location_country;

    @DatabaseField
    private int favorite_festival;

    @DatabaseField
    private int festival_status;

    @DatabaseField
    private boolean is_favorite_festival;

    public Festival(){}

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getFestival_name() {
        return festival_name;
    }

    public void setFestival_name(String festival_name) {
        this.festival_name = festival_name;
    }

    public String getFestival_logo() {
        return festival_logo;
    }

    public void setFestival_logo(String festival_logo) {
        this.festival_logo = festival_logo;
    }

    public int getFavorite_festival() {
        return favorite_festival;
    }

    public void setFavorite_festival(int favorite_festival) {
        this.favorite_festival = favorite_festival;
    }

    public String getFestival_description() {
        return festival_description;
    }

    public void setFestival_description(String festival_description) {
        this.festival_description = festival_description;
    }

    public String getFestival_homepage_url() {
        return festival_homepage_url;
    }

    public void setFestival_homepage_url(String festival_homepage_url) {
        this.festival_homepage_url = festival_homepage_url;
    }

    public int getFestival_status() {
        return festival_status;
    }

    public void setFestival_status(int festival_status) {
        this.festival_status = festival_status;
    }

    public boolean is_favorite_festival() {
        return is_favorite_festival;
    }

    public void setIs_favorite_festival(boolean is_favorite_festival) {
        this.is_favorite_festival = is_favorite_festival;
    }

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) {
        this.location_country = location_country;
    }
}
