package eu.fest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.FriendItem;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.FriendAdapter;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@EActivity(R.layout.activity_friend_search)
public class FriendSearchActivity extends BaseActionBarActivity implements NavigationDrawerCallbacks {

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @ViewById
    GridView search_list;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    EditText filter_input;

    String searchtext;

    int searchpage;

    FriendAdapter friendadapter;

    private CurrentUser localuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchtext="asd";
        searchpage=1;
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

        filter_input = (EditText) toolbar_actionbar.findViewById(R.id.filter_input);

        filter_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String asd = searchtext;
                searchtext = s.toString();
                if (!asd.equals(searchtext) && searchtext.length()>3) {
                   searchUser(searchpage+"", searchtext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void searchUser(String currentUser, String searchPhrase){
        webServerService.searchFriends(currentUser, searchPhrase, new Callback<FriendItem>() {
            @Override
            public void success(FriendItem friendItem, Response response) {
                friendadapter = new FriendAdapter(FriendSearchActivity.this);
                String json = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject data = new JSONObject(json);
                    JSONObject message = new JSONObject(data.getString("message"));
                    Object asd = message.get("rows");
                    if (!(asd instanceof JSONArray)) {
                        if (message.has("rows") && !message.getString("rows").isEmpty()) {
                            JSONObject jsonObject2 = new JSONObject(message.getString("rows"));
                            Iterator<String> iter = jsonObject2.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                JSONObject maps = new JSONObject(jsonObject2.getString(key));
                                FriendItem j = getUserFromJSON(maps);
                                friendadapter.add(j);
                            }
                        }
                    }else{
                        JSONArray jsonObject4 = message.getJSONArray("rows");
                        for (int u = 1; u < jsonObject4.length() + 1; u++) {
                            JSONObject maps = jsonObject4.getJSONObject(u - 1);
                            FriendItem j = getUserFromJSON(maps);
                            friendadapter.add(j);
                        }
                    }
                    search_list.setAdapter(friendadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Success", "asd");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
