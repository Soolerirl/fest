package eu.fest.model.databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "FestivalCountrys")
public class FestivalCountrys implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String location_country;

    public FestivalCountrys(){}

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) {
        this.location_country = location_country;
    }
}
