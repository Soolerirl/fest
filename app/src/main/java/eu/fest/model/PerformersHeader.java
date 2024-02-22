package eu.fest.model;

import java.util.ArrayList;

import eu.fest.model.databases.Performers;
import it.sephiroth.android.library.widget.HListView;

public class PerformersHeader extends Model {

    private String Date;

    private String Day;

    private ArrayList<Performers> items;

    public PerformersHeader(){}

    public PerformersHeader(String Date, String Day, ArrayList<Performers> items){
        this.Date = Date;
        this.Day = Day;
        this.items = items;
    }

    public ArrayList<Performers> getItems() {
        return items;
    }

    public void setItems(ArrayList<Performers> items) {
        this.items = items;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }
}
