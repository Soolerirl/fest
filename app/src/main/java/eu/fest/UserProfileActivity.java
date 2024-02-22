package eu.fest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.FriendItem;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.FriendAdapter;
import eu.fest.presentation.UserProfileEventListAdapter;
import eu.fest.presentation.UserProfileFestivalListAdapter;
import eu.fest.service.WebServerService;
import eu.fest.view.ExpGridView;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_user_profile)
public class UserProfileActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks{

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @Extra("user")
    CurrentUser user;

    @Extra("user_id")
    int user_id;

    FriendItem mainfriendItem;

    @ViewById
    CircleImageView profile_image;

    @ViewById
    TextView user_full_name;

    @ViewById
    LinearLayout friend_request;

    @ViewById
    ExpGridView like_festival_list;

    @ViewById
    ExpGridView goon_festival_list;

    @ViewById
    HListView friends_list;

    @ViewById
    TextView friend_request_txt;

    FriendAdapter friendadapter;

    UserProfileFestivalListAdapter adapter;

    UserProfileEventListAdapter eventAdapter;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @AfterViews
    void setup(){
        if(user != null){
            friend_request.setVisibility(View.GONE);
            /*byte[] decodedString = Base64.decode(user.getProfileImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profile_image.setImageBitmap(decodedByte);*/

            if(user.getProfileImage() == null || user.getProfileImage().equals(null) || user.getProfileImage() == "null" || user.getProfileImage().equals("null")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profile_image.setBackground(getResources().getDrawable(R.drawable.male_avatar));
                }
            }else{
                if(user.getProfileImage()!=null) {
                    Picasso.with(UserProfileActivity.this).load(user.getProfileImage().startsWith("http") ? user.getProfileImage() : "http://" + user.getProfileImage()).into(profile_image);
                }
            }

            user_full_name.setText(user.getFirstName() + " " + user.getLastName());

            adapter = new UserProfileFestivalListAdapter(this);
            for(Festival festival : getLikedFestival()){
                adapter.add(festival);
            }
            like_festival_list.setAdapter(adapter);
            like_festival_list.setExpanded(true);

            eventAdapter = new UserProfileEventListAdapter(this);
            for(Events events : getGoonEvent()){
                eventAdapter.add(events);
            }
            goon_festival_list.setAdapter(adapter);
            goon_festival_list.setExpanded(true);

            showFriends();
        }else if (user_id > 0){
            friend_request.setVisibility(View.VISIBLE);
            webServerService.getUserProfile(user_id + "", new Callback<FriendItem>() {
                @Override
                public void success(FriendItem friendItem, Response response) {
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject data = new JSONObject(json);
                        Object asd = data.get("message");
                        if(asd instanceof JSONObject) {
                            JSONObject message = new JSONObject(data.getString("message"));

                            JSONObject user = new JSONObject(message.getString("user_data"));
                            mainfriendItem = getFriendFromJSON(user);
                            if(mainfriendItem.getProfileImage() == null || mainfriendItem.getProfileImage().equals(null) || mainfriendItem.getProfileImage() == "null" || mainfriendItem.getProfileImage().equals("null")){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    profile_image.setBackground(getResources().getDrawable(R.drawable.male_avatar));
                                }
                            }else{
                                if(mainfriendItem.getProfileImage()!=null) {
                                    Picasso.with(UserProfileActivity.this).load(mainfriendItem.getProfileImage().startsWith("http") ? mainfriendItem.getProfileImage() : "http://" + mainfriendItem.getProfileImage()).into(profile_image);
                                }
                            }
                            user_full_name.setText(mainfriendItem.getFirstName() + " " + mainfriendItem.getLastName());
                            switch (mainfriendItem.getYourfriend()){
                                case 1:
                                    //Nem vagytok barátok
                                    friend_request_txt.setText(R.string.have_no_friends);
                                    break;
                                case 2:
                                    //Barátnak jelölted
                                    friend_request_txt.setText(R.string.friends_marked);
                                    break;
                                case 3:
                                    //Barátnak jelölt
                                    friend_request_txt.setText(R.string.marked_friend);
                                    break;
                                case 4:
                                    //barátok vagytok
                                    friend_request_txt.setText(R.string.friends_youare);
                                    break;
                                default:
                                    friend_request_txt.setText(R.string.have_no_friends);
                                    break;
                            }


                            JSONObject favorite_festivals = new JSONObject(message.getString("favorite_festivals"));

                            Iterator<String> iter = favorite_festivals.keys();
                            adapter = new UserProfileFestivalListAdapter(UserProfileActivity.this);
                            while (iter.hasNext()) {
                                String key = iter.next();
                                JSONObject festivals = new JSONObject(favorite_festivals.getString(key));
                                Festival festival = getFestivalFromJSON(festivals);
                                adapter.add(festival);
                            }
                            like_festival_list.setAdapter(adapter);
                            like_festival_list.setExpanded(true);


                            JSONObject going_festival_events = new JSONObject(message.getString("going_festival_events"));

                            Iterator<String> goiter = going_festival_events.keys();
                            eventAdapter = new UserProfileEventListAdapter(UserProfileActivity.this);
                            while (goiter.hasNext()) {
                                String key = goiter.next();
                                JSONObject events = new JSONObject(going_festival_events.getString(key));
                                Events event = getEventFromJSON(events);
                                eventAdapter.add(event);
                            }

                            goon_festival_list.setAdapter(adapter);
                            goon_festival_list.setExpanded(true);

                            if(message.has("friends")) {
                                JSONObject jsonObject2 = new JSONObject(message.getString("friends"));
                                Iterator<String> fiter = jsonObject2.keys();
                                friendadapter = new FriendAdapter(UserProfileActivity.this);
                                while (fiter.hasNext()) {
                                    String key = fiter.next();
                                    JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                    FriendItem j = getUserFromJSON(maps);
                                    friendadapter.add(j);
                                }

                                friends_list.setAdapter(friendadapter);
                            }

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
    }

    public FriendItem getFriendFromJSON(JSONObject jsonObject)throws JSONException{
        FriendItem friendItem = new FriendItem();
        String data = "";
        data = jsonObject.getString("user_id");
        friendItem.setUser_id(Integer.parseInt(data));
        data = jsonObject.getString("first_name");
        friendItem.setFirstName(data);
        data = jsonObject.getString("last_name");
        friendItem.setLastName(data);
        data = jsonObject.getString("friend_status_code");
        friendItem.setYourfriend(Integer.parseInt(data));
        data = jsonObject.getString("profile_image");
        friendItem.setProfileImage(data);

        return friendItem;
    }

    public Festival getFestivalFromJSON(JSONObject jsonObject)throws JSONException{
        Festival festival = new Festival();
        String data = "";
        data = jsonObject.getString("festival_id");
        festival.setServerId(Integer.parseInt(data));
        data = jsonObject.getString("festival_name");
        festival.setFestival_name(data);
        data = jsonObject.getString("festival_logo");
        festival.setFestival_logo(data);

        return festival;
    }

    public Events getEventFromJSON(JSONObject jsonObject)throws JSONException{
        Events events = new Events();
        String data = "";
        data = jsonObject.getString("festival_id");
        events.setFestivalId(Integer.parseInt(data));
        data = jsonObject.getString("festival_event_id");
        events.setServerId(Integer.parseInt(data));
        data = jsonObject.getString("festival_event_name");
        events.setFestival_event_name(data);
        data = jsonObject.getString("festival_event_logo");
        events.setFestival_event_logo(data);

        return events;
    }



    private Collection<Festival> getLikedFestival() {
        try {
            return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().eq("is_favorite_festival",true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Festival>();
    }

    private Collection<Events> getGoonEvent() {
        try {
            return db.getDatabaseHelper().getEventsDataDao().queryBuilder().where().eq("going_festival_event",true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Events>();
    }

    @Click(R.id.friend_request)
    void friendRequestClickHandle(){
        switch (mainfriendItem.getYourfriend()){
            case 1:
                addFriend();
                break;
            case 2:
                deleteFriend();
                break;
            case 3:
                addFriend();
                break;
            case 4:
                deleteFriend();
                break;
            default:
                addFriend();
                break;
        }

    }

    void deleteFriend(){
        webServerService.deleteFriend(mainfriendItem, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                UserProfileActivity_.intent(UserProfileActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION).user_id(mainfriendItem.getUser_id()).start();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    void addFriend(){
        webServerService.addFriend(mainfriendItem, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                UserProfileActivity_.intent(UserProfileActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION).user_id(mainfriendItem.getUser_id()).start();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }


    public void showFriends(){
        friendadapter = new FriendAdapter(UserProfileActivity.this);

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
                                Iterator<String> iter = jsonObject2.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                    FriendItem j = getUserFromJSON(maps);
                                    friendadapter.add(j);
                                }
                            }
                        } else {
                            JSONArray jsonObject4 = message.getJSONArray("friends");
                            for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                FriendItem j = getUserFromJSON(maps);
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
}
