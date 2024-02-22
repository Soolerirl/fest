package eu.fest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.table.TableUtils;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.FriendItem;
import eu.fest.model.Person;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Pins;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.FriendAdapter;
import eu.fest.service.WebServerService;

import eu.fest.view.ContactsCompletionView;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_map)
public class MapActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks,OnMapReadyCallback, LocationListener, TokenCompleteTextView.TokenListener {

    private static final int GOOGLE_MAP_CAMERA_ZOOM_LEVEL = 15;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @FragmentById(R.id.mapFragment)
    SupportMapFragment mapFragment;

    @Extra("eventData")
    Events eventData;

    @Extra("festival_event_id")
    int festival_event_id;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    Dialog dialog;

    private Dialog mDialog;

    TextView text;

    ContactsCompletionView completionView;

    ArrayList<Person> people = new ArrayList<Person>();
    ArrayList<FriendItem> friends = new ArrayList<FriendItem>();
    ArrayAdapter<Person> adapter;

    FriendAdapter friendadapter;

    CurrentUser currentUser;

    GoogleMap basemap;

    boolean isClicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), Pins.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(CurrentUser currentUser : getCurrentUser()){
            this.currentUser = currentUser;
        }

        if(festival_event_id>0){
            for(Events events : getEvents(festival_event_id)){
                eventData = events;
            }
        }

        showFriends();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapFragment.getMap().setMyLocationEnabled(true);
        mapFragment.getMapAsync(this);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //locationManager = null;

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        LatLng latLng = new LatLng(eventData.getLatitude(), eventData.getLongitude());
        if(eventData.getLatitude() == 0 && eventData.getLongitude() == 0){
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        if (mapFragment != null && mapFragment.getMap() != null) {
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mapFragment.getMap().animateCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_CAMERA_ZOOM_LEVEL));
        }
    }

    @Click(R.id.friend_pins_btn)
    void showfriendsPin(){
        if(isClicked){
            isClicked = false;
            addPinfromDb(basemap, true);
        }else{
            isClicked = true;
            addPinfromDb(basemap, false);
        }
    }

    public void showFriends(){
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
                                    friends.add(j);
                                    people.add(new Person(j.getFirstName() + " " + j.getLastName()));
                                }
                            }
                        } else {
                            JSONArray jsonObject4 = message.getJSONArray("friends");
                            for (int u = 1; u < jsonObject4.length() + 1; u++) {
                                JSONObject maps = jsonObject4.getJSONObject(u - 1);
                                FriendItem j = getUserFromJSON(maps);
                                friends.add(j);
                                people.add(new Person(j.getFirstName() + " " + j.getLastName()));
                            }
                        }
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

    public void setupautocomp(){

        adapter = new FilteredArrayAdapter<Person>(MapActivity.this, R.layout.person_layout, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }

                Person p = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(p.getName());
                //((TextView)convertView.findViewById(R.id.email)).setText(p.getEmail());

                return convertView;
            }

            @Override
            protected boolean keepObject(Person person, String mask) {
                mask = mask.toLowerCase();
                return person.getName().toLowerCase().startsWith(mask);
            }
        };


        completionView.setAdapter(adapter);
        completionView.setTokenListener(this);
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();

        double longitude = location.getLongitude();

        //LatLng latLng = new LatLng(latitude, longitude);
        LatLng latLng = new LatLng(eventData.getLatitude(), eventData.getLongitude());

        if (mapFragment != null && mapFragment.getMap() != null) {
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mapFragment.getMap().animateCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_CAMERA_ZOOM_LEVEL));
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        db = new DatabaseManager(getBaseContext());
        basemap = map;
        getPins(map);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View asd = getLayoutInflater().inflate(R.layout.pin_info_content, null);
                TextView pin_name = (TextView) asd.findViewById(R.id.pin_name);
                TextView pin_description = (TextView) asd.findViewById(R.id.pin_description);
                ImageView pin_type = (ImageView) asd.findViewById(R.id.pin_type);
                HListView friends_list = (HListView) asd.findViewById(R.id.friends_list);
                String[] markertext = convertStringToArray(marker.getSnippet(), "_,_");
                pin_name.setText(markertext[0]);
                pin_description.setText(markertext[1]);

                int pin_type_num = Integer.parseInt(markertext[2]);
                switch (pin_type_num){
                    case 2:
                        pin_type.setBackgroundResource(R.drawable.pin_create_tempt);
                        break;
                    case 3:
                        pin_type.setBackgroundResource(R.drawable.pin_create_stage);
                        break;
                    case 4:
                        pin_type.setBackgroundResource(R.drawable.pin_create_toilet);
                        break;
                    case 5:
                        pin_type.setBackgroundResource(R.drawable.pin_create_food);
                        break;
                    case 6:
                        pin_type.setBackgroundResource(R.drawable.pin_create_meet);
                        break;
                    default:
                        break;
                }
                friendadapter = new FriendAdapter(MapActivity.this);
                String users[] = convertStringToArray(markertext[3],"_;_");
                for(int i = 0;i<users.length;i++){
                    Log.d("List", users[i]);
                    for(FriendItem friendItem : friends){
                        if(users[i] != "null" && !users[i].equals("null") && users[i] != null && !users[i].equals(null))
                            Log.d("List", friendItem.getUser_id() +" , "+users[i]);
                        if(friendItem.getUser_id()==Integer.parseInt(users[i])){
                            friendadapter.add(friendItem);
                        }
                    }
                }
                friends_list.setAdapter(friendadapter);
                return asd;
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet() == null) {
                    marker.hideInfoWindow();
                    map.moveCamera(CameraUpdateFactory.zoomIn());
                    return true;
                }
                marker.showInfoWindow();
                return true;
            }
        });


        map.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(final Marker marker) {
                dialog = new Dialog(MapActivity.this);
                dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.pin_modify);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                Pins pin = new Pins();
                Log.d("Pins", marker.getTitle());
                for(Pins asd : collectPins(marker.getTitle())){
                    pin=asd;
                }

                final LatLng latLng = new LatLng(pin.getLatitude(), pin.getLongitude());
                final int[] pinicon = {R.drawable.pin_tempt};

                final EditText pin_name = (EditText) dialog.findViewById(R.id.pin_name);
                final EditText pin_description = (EditText) dialog.findViewById(R.id.pin_description);

                RadioButton pin_tempt = (RadioButton) dialog.findViewById(R.id.pin_tempt);
                final Pins finalPin = pin;
                pin_tempt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_tempt;
                        finalPin.setLocation_type(2);
                    }
                });

                RadioButton pin_stage = (RadioButton) dialog.findViewById(R.id.pin_stage);
                pin_stage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_stage;
                        finalPin.setLocation_type(3);
                    }
                });

                RadioButton pin_toilet = (RadioButton) dialog.findViewById(R.id.pin_toilet);
                pin_toilet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_toilet;
                        finalPin.setLocation_type(4);
                    }
                });

                RadioButton pin_food = (RadioButton) dialog.findViewById(R.id.pin_food);
                pin_food.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_food;
                        finalPin.setLocation_type(5);
                    }
                });

                RadioButton pin_meet = (RadioButton) dialog.findViewById(R.id.pin_meet);
                pin_meet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_meet;
                        finalPin.setLocation_type(6);
                    }
                });

                ImageButton btn_cancel = (ImageButton) dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                switch (pin.getLocation_type()){
                    case 2:
                        pinicon[0] = R.drawable.pin_tempt;
                        pin_tempt.setChecked(true);
                        break;
                    case 3:
                        pinicon[0] = R.drawable.pin_stage;
                        pin_stage.setChecked(true);
                        break;
                    case 4:
                        pinicon[0] = R.drawable.pin_toilet;
                        pin_toilet.setChecked(true);
                        break;
                    case 5:
                        pinicon[0] = R.drawable.pin_food;
                        pin_food.setChecked(true);
                        break;
                    case 6:
                        pinicon[0] = R.drawable.pin_meet;
                        pin_meet.setChecked(true);
                        break;
                    default:
                        break;
                }

                Log.d("pindata", pin.getLocation_name()+" , "+pin.getLocation_description());

                pin_name.setText(pin.getLocation_name());
                pin_description.setText(pin.getLocation_description());

                completionView = (ContactsCompletionView)dialog.findViewById(R.id.pin_add_friends);
                setupautocomp();

                Button pin_delete = (Button) dialog.findViewById(R.id.pin_delete);
                pin_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webServerService.deleteLocation(eventData, finalPin, new Callback<Pins>() {
                            @Override
                            public void success(Pins pins, Response response) {
                                try {
                                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                                    JSONObject data = new JSONObject(json);
                                    boolean success = Boolean.parseBoolean(data.getString("success"));
                                    if(success) {
                                        dialog.dismiss();
                                        marker.remove();
                                        try {
                                            db.getDatabaseHelper().getPinsDataDao().delete(finalPin);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        openDialog(data.getString("message"));
                                    }
                                } catch (JSONException e) {
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
                });

                Button pin_change = (Button) dialog.findViewById(R.id.pin_change);
                pin_change.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        marker.remove();

                        if(isFilled(pin_name)){
                            finalPin.setLocation_name(pin_name.getText().toString());
                        }else{
                            openDialog("Név megadás kötelező");
                            return;
                        }
                        ArrayList<Integer> asd = new ArrayList<Integer>();
                        if(completionView.getObjects().size()>0) {
                            for (int i = 0; i < completionView.getObjects().size(); i++) {
                                for (FriendItem item : friends) {
                                    if ((item.getFirstName() + " " + item.getLastName()).equals(completionView.getObjects().get(i).getName())) {
                                        asd.add(item.getUser_id());
                                    }
                                }
                            }
                            finalPin.setAddedUsersId(asd);
                        }


                        finalPin.setLocation_description(pin_description.getText().toString());
                        finalPin.setLongitude(latLng.longitude);
                        finalPin.setLatitude(latLng.latitude);

                        webServerService.changeLocation(eventData, finalPin, new Callback<Pins>() {
                            @Override
                            public void success(Pins pins, Response response) {
                                Log.d("Succes", "asd");
                                try {
                                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                                    JSONObject data = new JSONObject(json);
                                    boolean success = Boolean.parseBoolean(data.getString("success"));
                                    if(success) {
                                        marker.remove();
                                        dialog.dismiss();
                                        map.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(pinicon[0]))
                                                .title(finalPin.getLocation_name())
                                                .snippet(finalPin.getLocation_name() + "_,_" + finalPin.getLocation_description() + "_,_" + finalPin.getLocation_type() + "_,_" + finalPin.getAddedUsersIdString())
                                                .position(latLng)
                                                .draggable(true));
                                        try {
                                            db.getDatabaseHelper().getPinsDataDao().update(finalPin);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        openDialog(data.getString("message"));
                                    }
                                } catch (JSONException e) {
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
                });
                dialog.getWindow().setLayout(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                Log.d("Map asd", latLng.latitude +" , "+ latLng.longitude);
                dialog = new Dialog(MapActivity.this);
                dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.pin_create);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                final Pins pin = new Pins();
                final int[] pinicon = {R.drawable.pin_tempt};
                pin.setLocation_type(2);

                final EditText pin_name = (EditText) dialog.findViewById(R.id.pin_name);
                final EditText pin_description = (EditText) dialog.findViewById(R.id.pin_description);

                RadioButton pin_tempt = (RadioButton) dialog.findViewById(R.id.pin_tempt);
                pin_tempt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_tempt;
                        pin.setLocation_type(2);
                    }
                });

                RadioButton pin_stage = (RadioButton) dialog.findViewById(R.id.pin_stage);
                pin_stage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_stage;
                        pin.setLocation_type(3);
                    }
                });

                RadioButton pin_toilet = (RadioButton) dialog.findViewById(R.id.pin_toilet);
                pin_toilet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_toilet;
                        pin.setLocation_type(4);
                    }
                });

                RadioButton pin_food = (RadioButton) dialog.findViewById(R.id.pin_food);
                pin_food.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_food;
                        pin.setLocation_type(5);
                    }
                });

                RadioButton pin_meet = (RadioButton) dialog.findViewById(R.id.pin_meet);
                pin_meet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinicon[0] = R.drawable.pin_meet;
                        pin.setLocation_type(6);
                    }
                });

                ImageButton btn_cancel = (ImageButton) dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                completionView = (ContactsCompletionView)dialog.findViewById(R.id.pin_add_friends);
                setupautocomp();

                Button btn_change = (Button) dialog.findViewById(R.id.dialogButtonOK);
                btn_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isFilled(pin_name)){
                            pin.setLocation_name(pin_name.getText().toString());
                        }else{
                            openDialog("Név megadás kötelező");
                            return;
                        }
                        ArrayList<Integer> asd = new ArrayList<Integer>();
                        if(completionView.getObjects().size()>0) {
                            for (int i = 0; i < completionView.getObjects().size(); i++) {
                                for (FriendItem item : friends) {
                                    if ((item.getFirstName() + " " + item.getLastName()).equals(completionView.getObjects().get(i).getName())) {
                                        asd.add(item.getUser_id());
                                    }
                                }
                            }
                            pin.setAddedUsersId(asd);
                        }

                        pin.setLocation_description(pin_description.getText().toString());
                        //pin.setLongitude(Double.parseDouble(new DecimalFormat("##.########").format(latLng.longitude).replace(",",".")));
                        pin.setLongitude(latLng.longitude);
                        pin.setLatitude(latLng.latitude);

                        webServerService.addLocation(eventData, pin, new Callback<Pins>() {
                            @Override
                            public void success(Pins pins, Response response) {
                                Log.d("Succes", "asd");
                                try {
                                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                                    JSONObject data = new JSONObject(json);
                                    boolean success = Boolean.parseBoolean(data.getString("success"));
                                    if(success) {
                                        dialog.dismiss();
                                        map.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(pinicon[0]))
                                                .title(pin.getLocation_name())
                                                .snippet(pin.getLocation_name() + "_,_" + pin.getLocation_description() + "_,_" + pin.getLocation_type() + "_,_" + pin.getAddedUsersIdString())
                                                .position(latLng)
                                                .draggable(true));
                                        pin.setEvent_id(eventData.getServerId());
                                    savePins(pin);
                                    }else{
                                        openDialog(data.getString("message"));
                                    }
                                } catch (JSONException e) {
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
                });
                dialog.getWindow().setLayout(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(final Marker marker) {
                Pins pin = new Pins();
                for(Pins asd : collectPins(marker.getTitle())){
                    pin=asd;
                }
                final int[] pinicon = {R.drawable.pin_tempt};
                pin.setLatitude(marker.getPosition().latitude);
                pin.setLongitude(marker.getPosition().longitude);
                final Pins finalPin = pin;
                switch (pin.getLocation_type()){
                    case 2:
                        pinicon[0] = R.drawable.pin_tempt;
                        break;
                    case 3:
                        pinicon[0] = R.drawable.pin_stage;
                        break;
                    case 4:
                        pinicon[0] = R.drawable.pin_toilet;
                        break;
                    case 5:
                        pinicon[0] = R.drawable.pin_food;
                        break;
                    case 6:
                        pinicon[0] = R.drawable.pin_meet;
                        break;
                    default:
                        break;
                }
                webServerService.changeLocation(eventData, pin, new Callback<Pins>() {
                    @Override
                    public void success(Pins pins, Response response) {
                        Log.d("Succes", "asd");
                        try {
                            String json = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject data = new JSONObject(json);
                            boolean success = Boolean.parseBoolean(data.getString("success"));
                            if(success) {
                                try {
                                    db.getDatabaseHelper().getPinsDataDao().update(finalPin);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                openDialog(data.getString("message"));
                            }
                        } catch (JSONException e) {
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
        });
    }

    public void addPinfromDb(GoogleMap map, boolean showFriends){
        map.clear();
        for (Pins pindata : collectPins(showFriends)) {
            Drawable myDrawable = getResources().getDrawable(R.drawable.pin_tempt);
            Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
            int pin_type_num = pindata.getLocation_type();
            Log.d("UserIDS", pindata.getCreated_user_id()+" , "+currentUser.getUser_id());
            if(pindata.getCreated_user_id()==currentUser.getUser_id()) {
                switch (pin_type_num) {
                    case 2:
                        myDrawable = getResources().getDrawable(R.drawable.pin_tempt);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 3:
                        myDrawable = getResources().getDrawable(R.drawable.pin_stage);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 4:
                        myDrawable = getResources().getDrawable(R.drawable.pin_toilet);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 5:
                        myDrawable = getResources().getDrawable(R.drawable.pin_food);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 6:
                        myDrawable = getResources().getDrawable(R.drawable.pin_meet);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    default:
                        break;
                }
            }else{
                switch (pin_type_num) {
                    case 2:
                        myDrawable = getResources().getDrawable(R.drawable.pin_friend_tempt);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 3:
                        myDrawable = getResources().getDrawable(R.drawable.pin_friend_stage);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 4:
                        myDrawable = getResources().getDrawable(R.drawable.pin_friend_toilet);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 5:
                        myDrawable = getResources().getDrawable(R.drawable.pin_friend_food);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    case 6:
                        myDrawable = getResources().getDrawable(R.drawable.pin_friend_meet);
                        myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                        break;
                    default:
                        break;
                }
            }
            LatLng latLng = new LatLng(pindata.getLatitude(),pindata.getLongitude());
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(myLogo))
                    .title(pindata.getLocation_name())
                    .snippet(pindata.getLocation_name() + "_,_" + pindata.getLocation_description() + "_,_" + pindata.getLocation_type() + "_,_" + pindata.getAddedUsersIdString())
                    .position(latLng)
                    .draggable(true));
        }
    }

    public static String[] convertStringToArray(String str, String strSeparator){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    MapActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).eventData(eventData).start();
                } else {
                    FestivalListActivity_.intent(getBaseContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
                }

            }
            break;
        }
    }

    private boolean savePins(Pins pins) {
        try {
            return db.getDatabaseHelper().getPinsDataDao().create(pins) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    private Collection<Pins> collectPins(String name) {
        try {
            //return db.getDatabaseHelper().getPinsDataDao().queryBuilder().where().eq("location_name", name).query();//.and().eq("event_id",eventData.getServerId()).query();
            return db.getDatabaseHelper().getPinsDataDao().queryBuilder().where().eq("location_name", name).and().eq("event_id",eventData.getServerId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Pins>();
    }

    private Collection<Pins> collectPins(boolean asd) {
        try {
            if(asd) {
                return db.getDatabaseHelper().getPinsDataDao().queryForAll();
            }else{
                return db.getDatabaseHelper().getPinsDataDao().queryBuilder().where().not().like("addedUsersIdString","%" + currentUser.getUser_id() +"_;_").query();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Pins>();
    }

    private Collection<Events> getEvents(int festival_event_id){
        try {
            return db.getDatabaseHelper().getEventsDataDao().queryBuilder().where().eq("serverId",festival_event_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<Events>();
    }

    private Collection<CurrentUser> getCurrentUser() {
        try {
            return db.getDatabaseHelper().getUserDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<CurrentUser>();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public void openDialog(String content){
        mDialog = new Dialog(MapActivity.this);
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

    private void updateTokenConfirmation() {
        StringBuilder sb = new StringBuilder("Current tokens:\n");
        for (Object token: completionView.getObjects()) {
            sb.append(token.toString());
            sb.append("\n");
        }
    }

    @Override
    public void onTokenAdded(Object token) {
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Object token) {
        updateTokenConfirmation();
    }

    public void getPins(final GoogleMap map){
        webServerService.getLocation(eventData, new Callback<Pins>() {
            @Override
            public void success(Pins pins, Response response) {
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    JSONObject message = new JSONObject(data.getString("message"));
                    Iterator<String> iter = message.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        JSONObject maps = new JSONObject(message.getString(key));
                        Pins pins1 = getPinsFromJSON(maps);
                        pins1.setEvent_id(eventData.getServerId());
                        savePins(pins1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addPinfromDb(map,false);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public Pins getPinsFromJSON(JSONObject pins) throws JSONException {
        Pins pin = new Pins();
        String data = "";
        data = pins.getString("location_name");
        pin.setLocation_name(data);
        data = pins.getString("location_description");
        pin.setLocation_description(data);
        data = pins.getString("latitude");
        pin.setLatitude(Double.parseDouble(data));
        data = pins.getString("longitude");
        pin.setLongitude(Double.parseDouble(data));
        data = pins.getString("location_type");
        pin.setLocation_type(Integer.parseInt(data));
        data = pins.getString("created_user_id");
        pin.setCreated_user_id(Integer.parseInt(data));



        if(pins.has("friends")) {
            JSONObject friends = new JSONObject(pins.getString("friends"));
            Iterator<String> iter = friends.keys();
            ArrayList<Integer> uId = new ArrayList<>();
            String asd = "";
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject maps = new JSONObject(friends.getString(key));
                FriendItem j = getUserFromJSON(maps);
                uId.add(j.getUser_id());
                asd += j.getUser_id() + "_;_";
            }
            Log.d("List", asd);
            pin.setAddedUsersIdString(asd);
            pin.setAddedUsersId(uId);
        }

        return pin;
    }

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
    }
}
