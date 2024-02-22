package eu.fest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.j256.ormlite.table.TableUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.PerformersHeader;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Performers;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.DatabaseManager_;
import eu.fest.presentation.PerformersAdapter;
import eu.fest.presentation.PerformersAdapterV2;
import eu.fest.presentation.PerformersListAdapter;
import eu.fest.service.WebServerService;
import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_performers_list)
public class PerformersListActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks {

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    PerformersAdapterV2 adapter;

    @Extra("eventData")
    Events eventData;

    @Extra("festivalId")
    int festivalId;

    @Extra("festivalEventId")
    int festivalEventId;

    @Extra("festival_event_id")
    int festival_event_id;

    @ViewById
    ExpandableListView listView;

    @ViewById
    TextView event_name;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    String searchtext;

    int filter_btns;

    String country;

    EditText filter_input;

    ImageButton by_time;

    ImageButton by_time_revert;

    DateFormat formater;
    DateFormat dayformater;

    Date eventstart;

    Date eventend;

    Date currentEnd;
    Date currentStart;

    ArrayAdapter<String> genreAdapter;

    ArrayList<PerformersHeader> GroupListItems;

    Spinner genreSpinner;

    String currentGen=" ";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        searchtext="";
        country="asd";
        filter_btns = 0;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(festival_event_id>0){
            for(Events events : getEvents(festival_event_id)){
                eventData = events;
            }
        }
        db = new DatabaseManager(this);
        toolbar_actionbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar_actionbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar_actionbar);
        mNavigationDrawerFragment.setActivity(this);
        mNavigationDrawerFragment.closeDrawer();
        mNavigationDrawerFragment.setWebServerService(webServerService);
        mNavigationDrawerFragment.setServiceBusApplication(app);

        ImageView festival4ever = (ImageView) toolbar_actionbar.findViewById(R.id.festival4ever);
        festival4ever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
            }
        });

        genreSpinner = (Spinner) toolbar_actionbar.findViewById(R.id.genreSpinner);
        genreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        event_name.setText(eventData.getFestival_event_name());
        by_time = (ImageButton) toolbar_actionbar.findViewById(R.id.by_time);
        by_time_revert = (ImageButton) toolbar_actionbar.findViewById(R.id.by_time_revert);
        filter_input = (EditText) toolbar_actionbar.findViewById(R.id.filter_input);
        filter_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String asd = searchtext;
                searchtext = s.toString();
                if (!asd.equals(searchtext)) {
                    setupPerformerList();
                    filter_btns = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        by_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 1) {
                    filter_btns = 0;
                }else{
                    filter_btns = 1;
                }
                //showFestivals();
            }
        });

        by_time_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 2) {
                    filter_btns = 0;
                }else{
                    filter_btns = 2;
                }
                //showFestivals();
            }
        });

        clearDb();
        PerformersSync();
        formater = new SimpleDateFormat("yyyy-MM-dd");
        dayformater = new SimpleDateFormat("EEEE");
        try {
            eventstart = formater.parse(convertStringToArray(eventData.getStart_date())[0]);
            currentStart = formater.parse(convertStringToArray(eventData.getStart_date())[0]);
            eventend = formater.parse(convertStringToArray(eventData.getEnd_date())[0]);
            currentEnd = formater.parse(convertStringToArray(eventData.getEnd_date())[0]);
            //Log.d("Events time", formater.format(eventstart) +" "+ formater.format(eventend));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //listView.setAdapter(getHorizontalScrollableItemAdapter());
    }


    void setupPerformerList(){
        if(GroupListItems!=null)
            GroupListItems.clear();
        GroupListItems = showPerformers();
        PerformersListAdapter adapter = new PerformersListAdapter(PerformersListActivity.this, GroupListItems);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private Collection<Performers> collectPerformers(String date,String currentGen) {
        try {
            if(currentGen.equals(" ")) {
                if(searchtext!=""){
                    return db.getDatabaseHelper().getPerformersDataDao().queryBuilder().where().eq("start_date", date).and().like("performer_name","%"+searchtext+"%").query();
                }else {
                    return db.getDatabaseHelper().getPerformersDataDao().queryBuilder().where().eq("start_date", date).query();
                }
            }else{
                if(searchtext!=""){
                    return db.getDatabaseHelper().getPerformersDataDao().queryBuilder().where().eq("start_date", date).and().like("performer_genre","%"+currentGen+" %").and().like("performer_name","%"+searchtext+"%").query();
                }else {
                    return db.getDatabaseHelper().getPerformersDataDao().queryBuilder().where().eq("start_date", date).and().like("performer_genre","%"+currentGen+" %").query();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Performers>();
    }


     public void PerformersSync() {
        db = DatabaseManager_.getInstance_(this);
        clearDb();
        webServerService.getPerformersList(festivalId+"", festivalEventId+"",new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                Log.d("asd", response.getStatus() + " , " + response.getReason());
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    if(data.getString("success").equals("true") ) {
                        JSONObject message = new JSONObject(data.getString("message"));
                        Iterator<String> iter = message.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            JSONObject performer = new JSONObject(message.getString(key));
                            getFestivalFromJSON(performer);
                        }
                        //showPerformers();
                        genreSpinner.setAdapter(genreAdapter);
                        genreSpinner.setSelection(0);
                        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                currentGen = genreAdapter.getItem(position);
                                //genreAdapter.notifyDataSetChanged();
                                setupPerformerList();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        //setupPerformerList();
                        //listView.setAdapter(getHorizontalScrollableItemAdapter());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    private boolean savePerformers(Performers performers) {
        try {
            return db.getDatabaseHelper().getPerformersDataDao().create(performers) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void clearDb() {
        try {
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), Performers.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getFestivalFromJSON(JSONObject jsonObject) throws JSONException {
        Performers performer = new Performers();
        String data = "";

        performer.setFestivalId(festivalId);

        data = jsonObject.getString("festival_event_event_id");
        performer.setEventId(Integer.parseInt(data));

        data = jsonObject.getString("start_date");
        String[] asd = convertStringToArray(data);
        performer.setStart_date(asd[0]);
        performer.setStart_time(asd[1]);

        data = jsonObject.getString("festival_event_location_id");
        if(data == "null")
            data = 0+"";
        performer.setFestival_event_location_id(Integer.parseInt(data));

        data = jsonObject.getString("location_name");
        performer.setLocation_name(data);

        data = jsonObject.getString("is_going");
        performer.setIsgoing(Boolean.parseBoolean(data));

        data = jsonObject.getString("count_going_festival_event_event");
        if(data == "null")
            data = 0+"";
        performer.setCount_going_festival_event_event(Integer.parseInt(data));

        ArrayList<String> datalist = new ArrayList<>();
        if(jsonObject.has("music_genres")) {
            data="";
            JSONArray genres = jsonObject.getJSONArray("music_genres");
            for (int u = 1; u < genres.length() + 1; u++) {
                JSONObject performersJSON = genres.getJSONObject(u - 1);
                String adat = performersJSON.getString("genre_name");
                datalist.add(adat);
                if (u == genres.length()) {
                    data += " "+performersJSON.getString("genre_name") + " ";
                } else {
                    data += " "+performersJSON.getString("genre_name") + " |";
                }
            }
            performer.setPerformer_genre(data);
        }

        JSONArray jsonObject1 = jsonObject.getJSONArray("performers");
        for (int u = 1; u < jsonObject1.length() + 1; u++) {
            JSONObject performersJSON = jsonObject1.getJSONObject(u - 1);
            data = performersJSON.getString("performer_id");
            performer.setServerId(Integer.parseInt(data));
            performer.setPerformer_id(Integer.parseInt(data));

            data = performersJSON.getString("performer_name");
            performer.setPerformer_name(data);

            data = performersJSON.getString("performer_category_name");
            performer.setPerformer_category_name(data);

            data = performersJSON.getString("performer_logo");
            performer.setPerformer_logo(data);

            if(genreAdapter.getCount()>0){
                if(datalist!=null)
                if(datalist.size()>0)
                for(String genre : datalist) {
                    boolean isExist=false;
                    for(int i=0; i<genreAdapter.getCount();i++){
                        if (genre.equals(genreAdapter.getItem(i))) {
                            isExist = true;
                        }
                    }
                    if(!isExist){
                        genreAdapter.add(genre);
                    }
                }
            }else{
                genreAdapter.add(" ");
            }
            savePerformers(performer);
        }
    }

    public static String[] convertStringToArray(String str){
        String[] arr = str.split(" ");
        return arr;
    }


    public ArrayList<PerformersHeader> showPerformers(){
        ArrayList<PerformersHeader> list = new ArrayList<PerformersHeader>();

        Comparator<Performers> PerformersCompare = null;
        switch (filter_btns) {
            case 0:
                PerformersCompare = new Comparator<Performers>() {
                    public int compare(Performers c1, Performers c2) {
                        return c1.getServerId() - c2.getServerId();
                    }
                };
                break;
            case 1:
                PerformersCompare = new Comparator<Performers>() {
                    public int compare(Performers c1, Performers c2) {
                        return c1.getServerId() - c2.getServerId();
                        //return c1.getFestival_name().toString().compareTo(c2.getFestival_name().toString());
                    }
                };
                break;
            case 2:
                PerformersCompare = new Comparator<Performers>() {
                    public int compare(Performers c1, Performers c2) {
                        return c1.getServerId() - c2.getServerId();
                        //return c2.getFestival_name().toString().compareTo(c1.getFestival_name().toString());
                    }
                };
                break;
            default:
                break;
        }

        currentEnd.setDate(eventend.getDate()+1);
        currentStart.setDate(eventstart.getDate());
        for (Date date = currentStart; date.before(currentEnd); date.setDate(date.getDate() + 1)) {
            List<Performers> asd = (List<Performers>) collectPerformers(formater.format(date),currentGen);
            HListView hListView = new HListView(PerformersListActivity.this);
            PerformersAdapterV2 adapter = new PerformersAdapterV2(this);
            ArrayList<Performers> ch_list0 = new ArrayList<Performers>();

            if (PerformersCompare != null)
                Collections.sort(asd, PerformersCompare);

            int i = 0;
            if(asd != null && !asd.isEmpty())
                //adapter.replaceWith(asd);
                for (Performers performers : asd) {
                    if(i>0){
                        //if(performers.getStart_time() == asd.get(i-1).getStart_time() && performers.getLocation_name() == asd.get(i-1).getLocation_name()){
                            adapter.add(performers);
                        /*}else{
                            hListView = new HListView(PerformersListActivity.this);
                            hListView.setAdapter(adapter);
                            //adapter = new PerformersAdapterV2(this);
                            ch_list0.add(hListView);
                        }*/
                    }else{
                        adapter.add(performers);
                        hListView = new HListView(PerformersListActivity.this);
                    }

                    Log.d("Performers", performers.getStart_date() + " , " + performers.getPerformer_name());
                    ch_list0.add(performers);
                    adapter.add(performers);
                    i++;
                    //listView.setAdapter(adapter);
                }


            //adapter = new PerformersAdapterV2(this);
            hListView.setAdapter(adapter);
            //Log.d("Hlist", hListView.getAdapter().getCount()+" : "+formater.format(date));


            list.add(new PerformersHeader(formater.format(date),dayformater.format(date),ch_list0));
        }

        return list;
    }

    private Collection<Events> getEvents(int festival_event_id){
        try {
            return db.getDatabaseHelper().getEventsDataDao().queryBuilder().where().eq("serverId",festival_event_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Events>();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
