package eu.fest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.table.TableUtils;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.fest.FestivalListActivity_;
import eu.fest.LoginActivity_;
import eu.fest.R;
import eu.fest.ServiceBusApplication;
import eu.fest.SettingsActivity_;
import eu.fest.UserFriendsActivity;
import eu.fest.UserFriendsActivity_;
import eu.fest.UserProfileActivity_;
import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.model.NavigationItem;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.presentation.DatabaseManager;
import eu.fest.presentation.NavigationDrawerAdapter;
import eu.fest.service.WebServerService;
import eu.fest.view.ScrimInsetsFrameLayout;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.leftmenu_fragment)
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "my_app_settings"; //TODO: change this to your file
    private NavigationDrawerCallbacks mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;

    private final static int MENU_FESTIVAL_LIST = 0;
    private final static int MENU_FRIENDS = 1;
    private final static int MENU_SETTINGS = 2;
    private final static int MENU_USER = 3;
    private final static int MENU_LOGOUT = 4;

    ActionBarActivity activity;

    @Bean
    WebServerService webServerService;

    @App
    ServiceBusApplication app;

    CircleImageView imgAvatar;

    TextView txtUsername;

    private CurrentUser localuser;

    private String gcmkey;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leftmenu_fragment, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        final List<NavigationItem> navigationItems = getMenu();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        db = new DatabaseManager(activity);
        for(CurrentUser user : getUser()){
            localuser = user;
        }

        if(localuser.getProfileImage() == null || localuser.getProfileImage().equals(null) || localuser.getProfileImage() == "null" || localuser.getProfileImage().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imgAvatar.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(localuser.getProfileImage()!=null) {
                Picasso.with(getContext()).load(localuser.getProfileImage().startsWith("http") ? localuser.getProfileImage() : "http://" + localuser.getProfileImage()).into(imgAvatar);
            }
        }
        txtUsername.setText(localuser.getFirstName() + " " + localuser.getLastName());

        //selectItem(mCurrentSelectedPosition);
        return view;
    }

    public void refresProfilePic(String profilepic){

        if(profilepic!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Picasso.with(getContext()).load(profilepic.startsWith("http") ? profilepic : "http://" + profilepic).into(imgAvatar);
            }
        }
        /*byte[] decodedString = Base64.decode(profilepic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgAvatar.setImageBitmap(decodedByte);*/
    }

    void clearDb() {
        try {
            TableUtils.clearTable(db.getDatabaseHelper().getConnectionSource(), CurrentUser.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Collection<CurrentUser> getUser() {
        try {
            return db.getDatabaseHelper().getUserDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<CurrentUser>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        if(mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout){
            mFragmentContainerView = (View) mFragmentContainerView.getParent();
        }
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.leftmenu_bg));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.on, R.string.off) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
            mDrawerLayout.openDrawer(mFragmentContainerView);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public List<NavigationItem> getMenu() {
        List<NavigationItem> items = new ArrayList<NavigationItem>();
        items.add(new NavigationItem(getResources().getString(R.string.festivals_list), getResources().getDrawable(R.drawable.menu_festival_list)));
        items.add(new NavigationItem(getResources().getString(R.string.friends), getResources().getDrawable(R.drawable.menu_friends)));
        items.add(new NavigationItem(getResources().getString(R.string.settings), getResources().getDrawable(R.drawable.menu_settings)));
        items.add(new NavigationItem(getResources().getString(R.string.profile), getResources().getDrawable(R.drawable.menu_user_data)));
        items.add(new NavigationItem(getResources().getString(R.string.logout), getResources().getDrawable(R.drawable.menu_logout)));
        return items;
    }

    /**
     * Changes the icon of the drawer to back
     */
    public void showBackButton() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Changes the icon of the drawer to menu
     */
    public void showDrawerButton() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mActionBarDrawerToggle.syncState();
    }

    void selectItem(int position) {
        //mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            //mCallbacks.onNavigationDrawerItemSelected(position);
        }
        switch (position){
            case MENU_FESTIVAL_LIST:
                FestivalListActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                break;
            case MENU_FRIENDS:
                UserFriendsActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_NEW_TASK).user(localuser).start();
                break;
            case MENU_SETTINGS:
                SettingsActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                break;
            case MENU_USER:
                UserProfileActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_NEW_TASK).user(localuser).start();
                break;
            case MENU_LOGOUT:
                User luser = new User();
                luser.setDeviceId(app.getRegistrationId());
                webServerService.logout(luser, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (user.isSuccess()) {
                            //mDialog.dismiss();
                            app.getSettings().setRememberme(false);
                            clearDb();
                            LoginActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
                        } else {
                            //mDialog.setMessage(user.getMessage());
                            //Log.d("Logout", user.getMessage());
                       }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Logout", error.getMessage());
                    }
                });
                break;
            default:
                break;
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public void setActivity(ActionBarActivity activity) {
        this.activity = activity;
    }

    public void setWebServerService(WebServerService webServerService){
        this.webServerService=webServerService;
    }

    public void setServiceBusApplication(ServiceBusApplication serviceBusApplication){
        app=serviceBusApplication;
    }

    public ActionBarActivity getCurrentActivity() {
        return activity;
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager)activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);

        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;

        } else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }
}