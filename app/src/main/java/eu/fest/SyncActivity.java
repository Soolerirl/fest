package eu.fest;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.table.TableUtils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.model.databases.FestivalCountrys;
import eu.fest.model.databases.NotificationList;
import eu.fest.model.databases.Performers;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.DatabaseManager_;
import eu.fest.service.WebServerService;
import eu.fest.utils.Settings;
import eu.fest.view.GifView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_sync)
public class SyncActivity extends BaseActivity {

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @ViewById
    GifView syncanim;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @Extra("user")
    CurrentUser user;

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseManager_.getInstance_(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user==null){
            for(CurrentUser currentUser: getUser()){
                user = currentUser;
            }
        }

        settings = app.getSettings();
        syncanim.setMovieResource(R.drawable.sync_slow);
        if(isInternetOn()) {
            clearDb();
            FestivalSync();
        }else{
            FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
        }
    }

    void clearDb() {
        try {
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), Festival.class);
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), Events.class);
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), Performers.class);
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), CurrentUser.class);
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), FestivalCountrys.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void FestivalSync() {
        db = DatabaseManager_.getInstance_(this);
        clearDb();
        webServerService.getFestivalList(new Callback<Festival>() {
            @Override
            public void success(Festival festival, Response response) {
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    JSONObject message = new JSONObject(data.getString("message"));
                    int festivalnumber = message.length() + 1;
                    for (int i = 1; i < festivalnumber; i++) {
                        JSONObject festivals = new JSONObject(message.getString("" + i + ""));
                        Festival k = getFestivalFromJSON(festivals);
                        saveFestival(k);
                    }
                    eventSync();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
                //if (error.getResponse().getStatus() == 401)
                if (error.getMessage().equals("401 Invalid permission"))
                LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
            }
        });
    }

    public void eventSync(){
        for(Festival festival : getFestival()) {
            webServerService.getEventList(festival.getServerId()+"",new Callback<Events>() {
                @Override
                public void success(Events events, Response response) {
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject data = new JSONObject(json);
                        Object asd = data.get("message");
                        if(asd instanceof JSONObject) {
                            JSONObject message = new JSONObject(data.getString("message"));
                            Iterator<String> iter = message.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                JSONObject performer = new JSONObject(message.getString(key));
                                Events j = getEventFromJSON(performer);
                                j.setFestivalId(Integer.parseInt(key));
                                for(Festival festival: getFestival(j.getFestivalId())){
                                    festival.setLocation_country(j.getLocation_country());
                                    try {
                                        db.getDatabaseHelper().getFestivalDataDao().update(festival);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                saveEvent(j);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.getMessage());
                    if (error.getMessage().equals("401 Invalid permission"))
                        LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
                }
            });
        }
        getSettings();
    }

    public void getSettings(){
        webServerService.getSettings(new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser currentUser, Response response) {
                settings.setAcceptFriendRequest(currentUser.getAccept_friend_request());
                settings.setAddLocation(currentUser.getAdd_location());
                settings.setChangeFestivalEvent(currentUser.getChanged_festival_event());
                settings.setChangeFestivalEventEvent(currentUser.getChanged_festival_event_event());
                settings.setFacebook(currentUser.isFacebook());
                settings.setFriendGoingFestivalEvent(currentUser.getFriend_going_festival_event());
                settings.setFriendRequest(currentUser.getFriend_request());
                settings.setGlobalNotification(currentUser.getGlobal_notifications());
                settings.setHavepassword(currentUser.isHave_password());
                settings.setLanguage(currentUser.getLanguage());
                settings.setLedLights(currentUser.getLed_lights());
                settings.setLocale(currentUser.getLocale());
                settings.setNotificationBeforeEventTime(currentUser.getNotice_before_event_time());
                settings.setNotificationSound(currentUser.getNotification_sound());
                settings.setRecommendFestival(currentUser.getRecommend_festival());
                settings.setRecommendFestivalEvent(currentUser.getRecommend_festival_event());
                settings.setRecommendFestivalEventEvent(currentUser.getRecommend_festival_event_event());
                settings.setRecommendFriend(currentUser.getRecommend_friend());
                settings.setSendCrashReports(currentUser.getSend_crash_reports());
                settings.setTwitter(currentUser.isTwitter());
                settings.setVibration(currentUser.getVibration());
                currentUser.setUser_id(user.getUser_id());
                saveUser(currentUser);
                getNotification();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
                if (error.getMessage() == "401 Invalid permission")
                    LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
            }
        });
    }

    public void getNotification(){
        webServerService.getNotific(new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    Object asd = data.get("message");
                    if(asd instanceof JSONObject) {
                        JSONObject message = new JSONObject(data.getString("message"));
                        Iterator<String> iter = message.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            JSONObject performer = new JSONObject(message.getString(key));
                            NotificationList j = getNotificationFromJson(performer);
                            boolean isAlive = false;
                            for (NotificationList notificationList : getNotificationList()) {
                                if (notificationList.getFestival_event_event_id() == j.getFestival_event_event_id()
                                        && notificationList.getFestival_event_id() == j.getFestival_event_id()
                                        && notificationList.getFestival_id() == j.getFestival_id()){
                                    isAlive = true;
                                }
                            }
                            if(isAlive){
                                try {
                                    db.getDatabaseHelper().getNotificationListsDataDao().update(j);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                saveNotification(j);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).start();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
                if (error.getMessage() == "401 Invalid permission")
                    LoginActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
            }
        });
    }

    public NotificationList getNotificationFromJson(JSONObject notiObject) throws JSONException {
        NotificationList notificationList = new NotificationList();
        String data = "";

        data = notiObject.getString("festival_event_event_id");
        if (data == "null")
            data = 0 + "";
        notificationList.setFestival_event_event_id(Integer.parseInt(data));
        data = notiObject.getString("festival_event_id");
        if (data == "null")
            data = 0 + "";
        notificationList.setFestival_event_id(Integer.parseInt(data));
        data = notiObject.getString("festival_id");
        if (data == "null")
            data = 0 + "";
        notificationList.setFestival_id(Integer.parseInt(data));
        data = notiObject.getString("start_date");
        notificationList.setStart_date(data);
        data = notiObject.getString("event_event_description");
        notificationList.setEvent_event_description(data);
        data = notiObject.getString("festival_event_location_id");
        if (data == "null")
            data = 0 + "";
        notificationList.setFestival_event_location_id(Integer.parseInt(data));
        data = notiObject.getString("location_name");
        notificationList.setLocation_name(data);

        if(notiObject.has("performers")) {
            JSONArray performers = new JSONArray(notiObject.getString("performers"));
            String ids = "";
            String names = "";
            for(int i=1; i<performers.length()+1; i++){
                JSONObject maps = performers.getJSONObject(i - 1);
                ids += maps.getString("performer_id")+"_;_";
                names += maps.getString("performer_name")+"_;_";
            }
            notificationList.setPerformer_ids(ids);
            notificationList.setPerformer_names(names);
        }

        return notificationList;
    }

    private boolean saveNotification(NotificationList notificationList) {
        try {
            return db.getDatabaseHelper().getNotificationListsDataDao().create(notificationList) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean saveUser(CurrentUser user) {
        try {
            return db.getDatabaseHelper().getUserDataDao().create(user) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean saveFestival(Festival festival) {
        try {
            return db.getDatabaseHelper().getFestivalDataDao().create(festival) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean saveEvent(Events event){
        try{
            return db.getDatabaseHelper().getEventsDataDao().create(event) == 1;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public Festival getFestivalFromJSON(JSONObject jsonObject) throws JSONException {
        Festival festival = new Festival();
        String data = "";

        data = jsonObject.getString("festival_id");
        festival.setServerId(Integer.parseInt(data));

        data = jsonObject.getString("festival_name");
        festival.setFestival_name(data);

        data = jsonObject.getString("festival_description");
        festival.setFestival_description(data);

        data = jsonObject.getString("festival_homepage_url");
        festival.setFestival_homepage_url(data);

        data = jsonObject.getString("festival_status");
        if (data == "null")
            data = 0 + "";
        festival.setFestival_status(Integer.parseInt(data));

        data = jsonObject.getString("is_favorite_festival");
        festival.setIs_favorite_festival(Boolean.parseBoolean(data));

        data = jsonObject.getString("favorite_festival");
        if (data == "null")
            data = 0 + "";

        festival.setFavorite_festival(Integer.parseInt(data));

        data = jsonObject.getString("festival_logo");
        festival.setFestival_logo(data);

        return festival;
    }

    public Events getEventFromJSON(JSONObject jsonObject)throws JSONException{
        Events event = new Events();
        String data = "";

        data = jsonObject.getString("festival_event_id");
        event.setServerId(Integer.parseInt(data));

        if(jsonObject.has("festival_event_name")) {
            data = jsonObject.getString("festival_event_name");
            event.setFestival_event_name(data);
        }

        if(jsonObject.has("start_date")) {
            data = jsonObject.getString("start_date");
            event.setStart_date(data);
        }

        if(jsonObject.has("end_date")) {
            data = jsonObject.getString("end_date");
            event.setEnd_date(data);
        }

        if(jsonObject.has("location_country")) {
            data = jsonObject.getString("location_country");
            if(getFestivalCountrys().size()>0) {
                if(getFestivalCountrys(data).size()<=0) {
                    FestivalCountrys asd = new FestivalCountrys();
                    asd.setLocation_country(data);
                    saveFestivalCountrys(asd);
                }
            }else{
                FestivalCountrys asd = new FestivalCountrys();
                asd.setLocation_country(data);
                saveFestivalCountrys(asd);
            }
            event.setLocation_country(data);
        }

        if(jsonObject.has("location_city")) {
            data = jsonObject.getString("location_city");
            event.setLocation_city(data);
        }

        if(jsonObject.has("festival_location")) {
            data = jsonObject.getString("festival_location");
            event.setFestival_location(data);
        }

        if(jsonObject.has("festival_event_description")) {
            data = jsonObject.getString("festival_event_description");
            if (data == "null")
                data = 0 + "";
            event.setFestival_event_description(data);
        }

        data = jsonObject.getString("is_watch_festival_event");
        if (data == "null")
            data = 0 + "";
        event.setIs_watch_festival_event(Boolean.parseBoolean(data));

        data = jsonObject.getString("is_going_festival_event");
        event.setIs_going_festival_event(Boolean.parseBoolean(data));

        data = jsonObject.getString("user_rating_festival_event");
        if (data == "null")
            data = 0 + "";
        event.setUser_rating_festival_event(Double.parseDouble(data));

        if(jsonObject.has("going_festival_event")) {
            data = jsonObject.getString("going_festival_event");
            if (data == "null")
                data = 0 + "";
            event.setGoing_festival_event(Integer.parseInt(data));
        }

        if(jsonObject.has("watch_festival_event")) {
            data = jsonObject.getString("watch_festival_event");
            if (data == "null")
                data = 0 + "";
            event.setWatch_festival_event(Integer.parseInt(data));
        }

        data = jsonObject.getString("rating_festival_event");
        if (data == "null")
            data = 0 + "";
        event.setRating_festival_event(Double.parseDouble(data));

        if(jsonObject.has("festival_event_logo")) {
            data = jsonObject.getString("festival_event_logo");
            event.setFestival_event_logo(data);
        }

        if(jsonObject.has("latitude") && jsonObject.has("longitude")){
            data = jsonObject.getString("latitude");
            if (data == "null")
                data = 0 + "";
            event.setLatitude(Double.parseDouble(data));

            data = jsonObject.getString("longitude");
            if (data == "null")
                data = 0 + "";
            event.setLongitude(Double.parseDouble(data));
        }

        if(jsonObject.has("going_friends")){
            JSONObject friends = new JSONObject(jsonObject.getString("going_friends"));
            Iterator<String> iter = friends.keys();
            data="";
            while (iter.hasNext()) {
                String key = iter.next();
                data=key+"_;_";
            }
            event.setGoingfriends(data);
        }

        return event;
    }

    private Collection<FestivalCountrys> getFestivalCountrys(String location) {
        try {
            return db.getDatabaseHelper().getFestivalCountrysDataDao().queryBuilder().where().eq("location_country", location).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<FestivalCountrys>();
    }

    private Collection<FestivalCountrys> getFestivalCountrys() {
        try {
            return db.getDatabaseHelper().getFestivalCountrysDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<FestivalCountrys>();
    }

    private Collection<Festival> getFestival(int festivalId) {
        try {
            return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().eq("serverId", festivalId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Festival>();
    }

    private Collection<CurrentUser> getUser() {
        try {
            return db.getDatabaseHelper().getUserDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<CurrentUser>();
    }

    private Collection<NotificationList> getNotificationList() {
        try {
            return db.getDatabaseHelper().getNotificationListsDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<NotificationList>();
    }

    private Collection<Festival> getFestival() {
        try {
            return db.getDatabaseHelper().getFestivalDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Festival>();
    }

    private boolean saveFestivalCountrys(FestivalCountrys festivalCountrys) {
        try {
            return db.getDatabaseHelper().getFestivalCountrysDataDao().create(festivalCountrys) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
