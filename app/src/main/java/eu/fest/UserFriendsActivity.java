package eu.fest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.FriendItem;
import eu.fest.model.databases.CurrentUser;
import eu.fest.presentation.FriendAdapter;
import eu.fest.service.WebServerService;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_user_friends)
public class UserFriendsActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks {

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Extra("user")
    CurrentUser user;

    @ViewById
    GridView friends_list;

    @ViewById
    HListView your_friend_request_list;

    @ViewById
    HListView friend_request_list;

    FriendAdapter friendadapter;

    FriendAdapter friendrequestoutgoingadapter;

    FriendAdapter friendrequestincomingadapter;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        showFriends();
    }

    @Click(R.id.new_friends)
    void newfriendClickHandle(){
        FriendSearchActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public void showFriends(){
        friendadapter = new FriendAdapter(UserFriendsActivity.this);
        friendrequestincomingadapter = new FriendAdapter(UserFriendsActivity.this);
        friendrequestoutgoingadapter = new FriendAdapter(UserFriendsActivity.this);

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
                            }else{
                                JSONArray jsonObject4 = message.getJSONArray("friends");
                                for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                    JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                    FriendItem j = getUserFromJSON(maps);
                                    friendadapter.add(j);
                                }
                            }
                        }
                    if(message.has("outgoing")){
                            Object asd = message.get("outgoing");
                            if (!(asd instanceof JSONArray)) {
                                if (message.has("outgoing") && !message.getString("outgoing").isEmpty()) {
                                    JSONObject jsonObject2 = new JSONObject(message.getString("outgoing"));
                                    Iterator<String> iter = jsonObject2.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                        FriendItem j = getUserFromJSON(maps);
                                        friendrequestoutgoingadapter.add(j);
                                    }
                                }
                            }else{
                                JSONArray jsonObject4 = message.getJSONArray("outgoing");
                                for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                    JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                    FriendItem j = getUserFromJSON(maps);
                                    friendrequestoutgoingadapter.add(j);
                                }
                            }
                        }
                    if (message.has("incoming")){
                            Object asd = message.get("incoming");
                            if (!(asd instanceof JSONArray)) {
                                if (message.has("incoming") && !message.getString("incoming").isEmpty()) {
                                    JSONObject jsonObject2 = new JSONObject(message.getString("incoming"));
                                    Iterator<String> iter = jsonObject2.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                        FriendItem j = getUserFromJSON(maps);
                                        friendrequestincomingadapter.add(j);
                                    }
                                }
                            }else{
                                JSONArray jsonObject4 = message.getJSONArray("incoming");
                                for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                    JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                    FriendItem j = getUserFromJSON(maps);
                                    friendrequestincomingadapter.add(j);
                                }
                            }
                        }

                    //friends_list.setAdapter(friendadapter);
                    friend_request_list.setAdapter(friendrequestoutgoingadapter);
                    your_friend_request_list.setAdapter(friendrequestincomingadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Success", "asd");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        friends_list.setAdapter(friendadapter);

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
