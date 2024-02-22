package eu.fest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.User;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.model.databases.FestivalCountrys;
import eu.fest.model.databases.NotificationList;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.DatabaseManager_;
import eu.fest.presentation.FestivalAdapter;
import eu.fest.service.AlarmReceiver;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_festival_list)
public class FestivalListActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks {


    @ViewById
    ListView festival_list;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Extra("user")
    User user;

    EditText filter_input;

    ImageButton by_abc;

    ImageButton by_abc_revert;

    ImageButton by_rate;

    ImageButton by_rate_revert;

    String searchtext;

    int filter_btns;

    String country;

    private FestivalAdapter adapter;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        searchtext="asd";
        country="asd";
        filter_btns = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        Spinner countrySpinner = (Spinner) toolbar_actionbar.findViewById(R.id.countrySpinner);

        final String cLanguage = FestivalListActivity.this.getResources().getConfiguration().locale.getCountry();
        Locale loc = new Locale("en",cLanguage);
        //loc.getDisplayCountry(loc);
        //final String currentLanguage = loc.getDisplayCountry(loc);
        final String currentLanguage = "All";
        final ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        int i=0;
        int selection = 0;
        List<FestivalCountrys> asd = (List<FestivalCountrys>) collectCountrys();

        Comparator<FestivalCountrys> FestivalCompare = new Comparator<FestivalCountrys>() {
            public int compare(FestivalCountrys c1, FestivalCountrys c2) {
                return c1.getLocation_country().toString().compareTo(c2.getLocation_country().toString());
            }
        };
        Collections.sort(asd, FestivalCompare);
        countryAdapter.add("All");
        for(FestivalCountrys festivalCountrys : asd){
            countryAdapter.add(festivalCountrys.getLocation_country());
            Log.d("Countrys", currentLanguage +" , " + festivalCountrys.getLocation_country());
            if(currentLanguage == festivalCountrys.getLocation_country() || currentLanguage.equals(festivalCountrys.getLocation_country())){
                selection = i;
                country = festivalCountrys.getLocation_country();
            }
            i++;
        }
        Log.d("Country", country);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setSelection(selection);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = countryAdapter.getItem(position);
                showFestivals(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filter_input = (EditText) toolbar_actionbar.findViewById(R.id.filter_input);
        by_abc = (ImageButton) toolbar_actionbar.findViewById(R.id.by_abc);
        by_abc_revert = (ImageButton) toolbar_actionbar.findViewById(R.id.by_abc_revert);
        by_rate = (ImageButton) toolbar_actionbar.findViewById(R.id.by_rate);
        by_rate_revert = (ImageButton) toolbar_actionbar.findViewById(R.id.by_rate_revert);
       // Log.d("user",user.getLastName() +" , "+user.getFirstName());
        showFestivals(country);



        filter_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String asd = searchtext;
                searchtext = s.toString();
                if (!asd.equals(searchtext)) {
                    showFestivals(country);
                    filter_btns = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        by_abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 1) {
                    filter_btns = 0;
                }else{
                    filter_btns = 1;
                }
                showFestivals(country);
            }
        });

        by_abc_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 2) {
                    filter_btns = 0;
                }else{
                    filter_btns = 2;
                }
                showFestivals(country);
            }
        });

        by_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 3) {
                    filter_btns = 0;
                }else{
                    filter_btns = 3;
                }
                showFestivals(country);
            }
        });

        by_rate_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_btns == 4) {
                    filter_btns = 0;
                }else{
                    filter_btns = 4;
                }
                showFestivals(country);
            }
        });

        setAlarms();
    }

    public void showFestivals(String country){
        db = new DatabaseManager(this);
        adapter = new FestivalAdapter(this);
        List<Festival> asd = (List<Festival>) collectFestivals(country);

        Comparator<Festival> FestivalCompare = null;

        switch (filter_btns){
            case 0:
                FestivalCompare = new Comparator<Festival>() {
                    public int compare(Festival c1, Festival c2) {
                        return c1.getServerId() - c2.getServerId();
                    }
                };
                break;
            case 1:
                FestivalCompare = new Comparator<Festival>() {
                    public int compare(Festival c1, Festival c2) {
                        return c1.getFestival_name().toString().compareTo(c2.getFestival_name().toString());
                    }
                };
                break;
            case 2:
                FestivalCompare = new Comparator<Festival>() {
                    public int compare(Festival c1, Festival c2) {
                        return c2.getFestival_name().toString().compareTo(c1.getFestival_name().toString());
                    }
                };
                break;
            case 3:
                FestivalCompare = new Comparator<Festival>() {
                    public int compare(Festival c1, Festival c2) {
                        return c1.getFavorite_festival() - c2.getFavorite_festival();
                    }
                };
                break;
            case 4:
                FestivalCompare = new Comparator<Festival>() {
                    public int compare(Festival c1, Festival c2) {
                        return c2.getFavorite_festival() - c1.getFavorite_festival();
                    }
                };
                break;
            default:
                break;
        }

        if(FestivalCompare != null)
        Collections.sort(asd, FestivalCompare);

        for(Festival festival : asd){
            adapter.add(festival);
        }
        festival_list.setAdapter(adapter);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    private Collection<Festival> collectFestivals(String country) {
        try {
            if(filter_input.getText().toString().length()>0){
                if(country.equals("asd")){
                    return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().like("festival_name", "%" + searchtext + "%").query();
                }else{
                    return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().like("festival_name", "%" + searchtext + "%").query();
                }
            }else {
                if(country.equals("All")){
                    return db.getDatabaseHelper().getFestivalDataDao().queryForAll();
                }else {
                    return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().eq("location_country", country).query();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Festival>();
    }

    private Collection<FestivalCountrys> collectCountrys(){
        try {
            return db.getDatabaseHelper().getFestivalCountrysDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<FestivalCountrys>();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        }else {
            super.onBackPressed();
        }
    }



    public void setAlarms(){
        for(NotificationList notificationList : getNotificationList()) {
            String ids[] = convertStringToArray(notificationList.getPerformer_ids(),"_;_");
            String performs[] = convertStringToArray(notificationList.getPerformer_names(),"_;_");
            String time[] = convertStringToArray(app.getSettings().getNotificationBeforeEventTime(),":");
            String performers = "";
            try {
                Date baseDate = formatter.parse(notificationList.getStart_date());
                Long beforeTime = Long.valueOf((Integer.parseInt(time[0])*1000*60*60) + (Integer.parseInt(time[1])*1000*60) + (Integer.parseInt(time[2])*1000));
                Long newDate = baseDate.getTime() - beforeTime;
                Date date = new Date(newDate);

                Date now = new Date(System.currentTimeMillis());

                if(date.after(now)) {
                    String asd[] = convertStringToArray(formatter.format(date), " ");
                    String dates[] = convertStringToArray(asd[0], "-");
                    String times[] = convertStringToArray(asd[1], ":");


                    Calendar cal = new GregorianCalendar();
                    cal.set(Calendar.YEAR, Integer.parseInt(dates[0]));
                    cal.set(Calendar.MONTH, (Integer.parseInt(dates[1]) - 1));
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(times[2]));

                    for (int i = 0; i < ids.length; i++) {
                        if (i == ids.length - 1) {
                            performers += performs[i];
                        } else {
                            performers += performs[i] + ", ";
                        }
                    }
                    Intent intent = new Intent(FestivalListActivity.this, AlarmReceiver.class);
                    intent.putExtra("performer", performers);
                    intent.putExtra("hour", time[0]);
                    intent.putExtra("minute", time[1]);

                    int intentId = Integer.parseInt(notificationList.getFestival_id() + "" + notificationList.getFestival_event_id() + "" + notificationList.getFestival_event_event_id() + ""
                            + notificationList.getFestival_event_location_id() + "" + ids[0]);

                    PendingIntent pi = PendingIntent.getBroadcast(this, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private Collection<NotificationList> getNotificationList() {
        try {
            return db.getDatabaseHelper().getNotificationListsDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<NotificationList>();
    }
}
