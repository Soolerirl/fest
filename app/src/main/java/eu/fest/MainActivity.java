package eu.fest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import eu.fest.callbacks.NavigationDrawerCallbacks;
import eu.fest.fragments.NavigationDrawerFragment;
import eu.fest.model.User;
import eu.fest.service.WebServerService;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    @Bean
    WebServerService webServerService;

    @App
    ServiceBusApplication app;

    private User localuser;

    private ProgressDialog mDialog;

    private Toolbar toolbar_actionbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localuser = new User();

    }

    @Override
    protected void onStart() {
        super.onStart();
        toolbar_actionbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar_actionbar.setTitle("Lista");
        setSupportActionBar(toolbar_actionbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar_actionbar);
        mNavigationDrawerFragment.setActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }
}
