package eu.fest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.FriendItem;
import eu.fest.model.databases.Events;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.FriendAdapter;
import eu.fest.service.WebServerService;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_event_preview)
public class EventPreviewActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks, AdapterView.OnItemClickListener {

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    private Toolbar toolbar_actionbar;

    private Dialog mDialog;

    TextView text;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Extra("eventData")
    Events eventData;

    @Extra("festival_event_id")
    int festival_event_id;

    @ViewById
    HListView friends_list;

    @ViewById
    ImageView event_pic;

    @ViewById
    TextView event_all_rating_number;

    @ViewById
    TextView event_name;

    @ViewById
    TextView event_description;

    @ViewById
    TextView event_location;

    @ViewById
    TextView event_time;

    @ViewById
    TextView event_is_active;

    @ViewById
    TextView follow_text;

    @ViewById
    TextView go_on_text;

    @ViewById
    TextView followtxt;

    @ViewById
    TextView goontxt;

    @ViewById
    RatingBar ratingBar;

    FriendAdapter friendadapter;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formattershow = new SimpleDateFormat("yyyy. MMMM dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if(eventData.getFestival_event_logo()!=null) {
            Picasso.with(EventPreviewActivity.this).load(eventData.getFestival_event_logo().startsWith("http") ? eventData.getFestival_event_logo() : "http://" + eventData.getFestival_event_logo()).into(event_pic);
        }
        event_name.setText(eventData.getFestival_event_name());

        event_location.setText(eventData.getLocation_country() + ", " + eventData.getLocation_city());
        //event_location.setText(eventData.getFestival_location());

        event_description.setText(eventData.getFestival_event_description()+"");

        try {
            Date startDate = formatter.parse(eventData.getStart_date());
            Date endDate = formatter.parse(eventData.getEnd_date());
            Date now = new Date(System.currentTimeMillis());
            String startDates = formattershow.format(startDate);
            String endDates[] = convertStringToArray(formattershow.format(endDate)," ");
            event_time.setText(startDates + "-"+endDates[2]);

            if(startDate.after(now) && endDate.before(now)){
                event_is_active.setVisibility(View.VISIBLE);
            }else{
                event_is_active.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        go_on_text.setText(eventData.getGoing_festival_event()+"");

        follow_text.setText(eventData.getWatch_festival_event()+"");

        if(eventData.is_watch_festival_event()) {
            followtxt.setText(getResources().getString(R.string.follow));
        }else{
            followtxt.setText(getResources().getString(R.string.not_follow));
        }

        if(eventData.is_going_festival_event()) {
            goontxt.setText(getResources().getString(R.string.attend));
        }else{
            goontxt.setText(getResources().getString(R.string.not_attend));
        }

        ratingBar.setRating((float) eventData.getRating_festival_event());
        event_all_rating_number.setText((int)eventData.getUser_rating_festival_event()+"");
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchPositionX = event.getX();
                float width = ratingBar.getWidth();
                final float starsf = (touchPositionX / width) * 5.0f;
                //int stars = (int)starsf + 1;
                ratingBar.setRating(starsf);
                eventData.setRating_festival_event(starsf);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    webServerService.rateFestivalEvent(eventData, new Callback<Events>() {
                        @Override
                        public void success(Events events, Response response) {
                            Log.d("Succes", "asd");
                            eventData.setRating_festival_event(starsf);
                            try {
                                db.getDatabaseHelper().getEventsDataDao().update(eventData);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                            openDialog(error.getMessage());
                        }
                    });
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }
                return true;
            }});
        if(eventData.getGoingfriends()!=null)
            showFriends();
    }

    public void showFriends(){
        friendadapter = new FriendAdapter(EventPreviewActivity.this);

        webServerService.getFriendRequests(new Callback<FriendItem>() {
            @Override
            public void success(FriendItem friendItem, Response response) {
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    JSONObject message = new JSONObject(data.getString("message"));
                    if(message.has("friends")) {
                        Object asd = message.get("friends");
                        if (!(asd instanceof JSONArray)) {
                            if (message.has("friends") && !message.getString("friends").isEmpty()) {
                                JSONObject jsonObject2 = new JSONObject(message.getString("friends"));
                                String adat[] = convertStringToArray(eventData.getGoingfriends(), "_;_");
                                Iterator<String> iter = jsonObject2.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                    FriendItem j = getUserFromJSON(maps);
                                    boolean isgoon = false;
                                    for (int i = 0; i <= adat.length; i++) {
                                        if (j.getUser_id() == Integer.parseInt(adat[i])) {
                                            isgoon = true;
                                        }
                                    }
                                    if (isgoon)
                                        friendadapter.add(j);
                                }
                            }
                        } else {
                            String adat[] = convertStringToArray(eventData.getGoingfriends(), "_;_");
                            JSONArray jsonObject4 = message.getJSONArray("friends");
                            for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                FriendItem j = getUserFromJSON(maps);
                                boolean isgoon = false;
                                for (int i = 0; i < adat.length; i++) {
                                    if (j.getUser_id() == Integer.parseInt(adat[i])) {
                                        isgoon = true;
                                    }
                                }
                                if (isgoon)
                                    friendadapter.add(j);
                            }
                        }
                        friends_list.setAdapter(friendadapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Success", "asd");
            }

            @Override
            public void failure(RetrofitError error) {
                openDialog(error.getMessage());
            }
        });
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    public FriendItem getUserFromJSON(JSONObject friends) throws JSONException {
        FriendItem friendItem = new FriendItem();
        String data = "";
        data = friends.getString("user_id");
        friendItem.setUser_id(Integer.parseInt(data));

        data = friends.getString("first_name");
        friendItem.setFirstName(data);

        data = friends.getString("last_name");
        friendItem.setLastName(data);

        data = friends.getString("profile_image_path");
        friendItem.setProfileImage(data);

        return friendItem;
    }


    @Click(R.id.performers_btn)
    void performersClickHandle(){
        PerformersListActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).festivalId(eventData.getFestivalId()).festivalEventId(eventData.getServerId()).eventData(eventData).start();
    }

    @Click(R.id.map_btn)
    void callFestivalAppClickHandle(){
        MapActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).eventData(eventData).start();
    }

    @Click(R.id.go_on_btn)
    void goOnClickHandle(){
        if(eventData.is_going_festival_event()){
            webServerService.deleteGoingFestivalEvent(eventData.getFestivalId()+"", eventData.getServerId()+"", new Callback<Events>() {
                @Override
                public void success(Events events, Response response) {
                    eventData.setIs_going_festival_event(false);
                    int a = eventData.getGoing_festival_event();
                    eventData.setGoing_festival_event((a-1));
                    try {
                        db.getDatabaseHelper().getEventsDataDao().update(eventData);
                        go_on_text.setText(eventData.getGoing_festival_event()+"");
                        goontxt.setText(getResources().getString(R.string.not_attend));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.getMessage());
                    openDialog(error.getMessage());
                }
            });
        }else{
            webServerService.addGoingFestivalEvent(eventData.getFestivalId()+"", eventData.getServerId()+"", new Callback<Events>() {
                @Override
                public void success(Events events, Response response) {
                    Log.d("Succes", " ");
                    eventData.setIs_going_festival_event(true);
                    int a = eventData.getGoing_festival_event();
                    eventData.setGoing_festival_event((a+1));
                    try {
                        db.getDatabaseHelper().getEventsDataDao().update(eventData);
                        go_on_text.setText(eventData.getGoing_festival_event()+"");
                        goontxt.setText(getResources().getString(R.string.attend));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.getMessage());
                    openDialog(error.getMessage());
                }
            });
        }
    }

    @Click(R.id.follow_btn)
    void followClickHandle(){
        if(eventData.is_watch_festival_event()){
            webServerService.deleteWatchFestivalEvent(eventData.getFestivalId() + "", eventData.getServerId() + "", new Callback<Events>() {
                @Override
                public void success(Events events, Response response) {
                    Log.d("Succes", " ");
                    eventData.setIs_watch_festival_event(false);
                    int a = eventData.getWatch_festival_event();
                    eventData.setWatch_festival_event((a-1));
                    try {
                        db.getDatabaseHelper().getEventsDataDao().update(eventData);
                        follow_text.setText(eventData.getWatch_festival_event()+"");
                        followtxt.setText(getResources().getString(R.string.not_follow));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.getMessage());
                    openDialog(error.getMessage());
                }
            });
        }else{
            webServerService.addWatchFestivalEvent(eventData.getFestivalId() + "", eventData.getServerId() + "", new Callback<Events>() {
                @Override
                public void success(Events events, Response response) {
                    Log.d("Succes", " ");
                    eventData.setIs_watch_festival_event(true);
                    int a = eventData.getWatch_festival_event();
                    eventData.setWatch_festival_event((a+1));
                    try {
                        db.getDatabaseHelper().getEventsDataDao().update(eventData);
                        follow_text.setText(eventData.getWatch_festival_event()+"");
                        followtxt.setText(getResources().getString(R.string.follow));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Error", error.getMessage());
                    openDialog(error.getMessage());
                }
            });
        }
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

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void openDialog(String content){
        mDialog = new Dialog(EventPreviewActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.message_dialog);
        mDialog.setCancelable(true);
        text = (TextView) mDialog.findViewById(R.id.text);
        text.setText(content);
        Button dialogButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
