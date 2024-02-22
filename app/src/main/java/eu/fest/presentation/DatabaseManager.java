package eu.fest.presentation;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;

import org.androidannotations.annotations.EBean;

@EBean
public class DatabaseManager {

    private ConnectionSource source = null;

    private Context context = null;

    private DatabaseHelper databaseHelper = null;

    public DatabaseManager(Context context){
        this.context = context;
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public DatabaseHelper getDatabaseHelper(){
        return databaseHelper;
    }
}
