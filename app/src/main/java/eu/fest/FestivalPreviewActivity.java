package eu.fest;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import eu.fest.Event.Event;
import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.EventAdapter;
import eu.fest.presentation.FestivalAdapter;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_festival_preview)
public class FestivalPreviewActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks {

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @ViewById
    TextView festival_name;

    @ViewById
    TextView like_text;

    @ViewById
    TextView like_all_text;

    @ViewById
    ImageView festival_logo;

    @ViewById
    LinearLayout festival_logo_bg;

    @ViewById
    ListView event_list;

    @Extra("fetivalData")
    Festival festivalData;

    @Extra("festival_id")
    int festival_id;

    Events event;

    private EventAdapter adapter;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();


        if(festival_id > 0){
            for(Festival festival: getFestival(festival_id)){
                festivalData = festival;
            }
        }

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
    void setupUi(){
        festival_id = festivalData.getServerId();
        festival_name.setText(festivalData.getFestival_name());
        if(festivalData.getFestival_logo()!=null) {
            Picasso.with(FestivalPreviewActivity.this).load(festivalData.getFestival_logo().startsWith("http") ? festivalData.getFestival_logo() : "http://" + festivalData.getFestival_logo()).into(festival_logo);
        }
        if(!festivalData.is_favorite_festival()){
            like_text.setText(R.string.not_liked);
        }else if(festivalData.is_favorite_festival()){
            like_text.setText(R.string.liked);
        }
        like_all_text.setText(festivalData.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
        showEvent();
    }

    @Click(R.id.like_button)
    void likebtnClickHandle(){
        if (!festivalData.is_favorite_festival()) {
            webServerService.addFavoriteFestival(festivalData.getServerId() + "", new Callback<Festival>() {
                @Override
                public void success(Festival festival, Response response) {
                    festivalData.setIs_favorite_festival(true);
                    int a = festivalData.getFavorite_festival();
                    festivalData.setFavorite_festival((a+1));
                    try {
                        db.getDatabaseHelper().getFestivalDataDao().update(festivalData);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    like_all_text.setText(festivalData.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
                    like_text.setText(R.string.liked);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Festival", error.getMessage());
                }
            });
        } else if (festivalData.is_favorite_festival()) {
            webServerService.deleteFavoriteFestival(festivalData.getServerId() + "", new Callback<Festival>() {
                @Override
                public void success(Festival festival, Response response) {
                    festivalData.setIs_favorite_festival(false);
                    int a = festivalData.getFavorite_festival();
                    festivalData.setFavorite_festival((a-1));
                    try {
                        db.getDatabaseHelper().getFestivalDataDao().update(festivalData);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    like_all_text.setText(festivalData.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
                    like_text.setText(R.string.not_liked);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Festival", error.getMessage());
                }
            });
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public void showEvent(){
        db = new DatabaseManager(this);
        adapter = new EventAdapter(this);
        event = new Events();
        for(Events event : collectEvent(festival_id)){
            adapter.add(event);
        }
        event_list.setAdapter(adapter);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    private Collection<Events> collectEvent(int festivalid) {
        try {
            return db.getDatabaseHelper().getEventsDataDao().queryBuilder().where().eq("festivalId", festivalid).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Events>();
    }

    private Collection<Festival> getFestival(int festivalid) {
        try {
            return db.getDatabaseHelper().getFestivalDataDao().queryBuilder().where().eq("serverId", festivalid).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Festival>();
    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        }else {
            super.onBackPressed();
        }
    }
}
