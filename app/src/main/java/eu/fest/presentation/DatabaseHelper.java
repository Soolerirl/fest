package eu.fest.presentation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.model.databases.FestivalCountrys;
import eu.fest.model.databases.NotificationList;
import eu.fest.model.databases.Performers;
import eu.fest.model.databases.Pins;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME = "festival4ever.db";
    private static final int DATABASE_VERSION = 6;

    private Dao<Festival, Integer> festivalDataDao;
    private Dao<Events, Integer> eventsDataDao;
    private Dao<Performers, Integer> performersDataDao;
    private Dao<CurrentUser, Integer> userDataDao;
    private Dao<Pins, Integer> pinsDataDao;
    private Dao<FestivalCountrys, Integer> festivalCountrysDataDao;
    private Dao<NotificationList, Integer> notificationListsDataDao;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource){
        try {
            TableUtils.createTable(connectionSource, Festival.class);
            TableUtils.createTable(connectionSource, Events.class);
            TableUtils.createTable(connectionSource, Performers.class);
            TableUtils.createTable(connectionSource, CurrentUser.class);
            TableUtils.createTable(connectionSource, Pins.class);
            TableUtils.createTable(connectionSource, FestivalCountrys.class);
            TableUtils.createTable(connectionSource, NotificationList.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Festival.class, true);
            TableUtils.dropTable(connectionSource, Events.class, true);
            TableUtils.dropTable(connectionSource, Performers.class, true);
            TableUtils.dropTable(connectionSource, CurrentUser.class, true);
            TableUtils.dropTable(connectionSource, Pins.class, true);
            TableUtils.dropTable(connectionSource, FestivalCountrys.class, true);
            TableUtils.dropTable(connectionSource, NotificationList.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Festival, Integer> getFestivalDataDao() throws SQLException{
        if(festivalDataDao == null){
            festivalDataDao = getDao(Festival.class);
        }
        return festivalDataDao;
    }

    public Dao<Events, Integer> getEventsDataDao() throws SQLException {
        if(eventsDataDao == null){
            eventsDataDao = getDao(Events.class);
        }
        return eventsDataDao;
    }

    public Dao<Performers, Integer> getPerformersDataDao() throws SQLException {
        if(performersDataDao == null){
            performersDataDao = getDao(Performers.class);
        }
        return performersDataDao;
    }

    public Dao<CurrentUser, Integer> getUserDataDao() throws SQLException {
        if(userDataDao == null){
            userDataDao = getDao(CurrentUser.class);
        }
        return userDataDao;
    }

    public Dao<Pins, Integer> getPinsDataDao() throws SQLException {
        if(pinsDataDao == null){
            pinsDataDao = getDao(Pins.class);
        }
        return pinsDataDao;
    }

    public Dao<FestivalCountrys, Integer> getFestivalCountrysDataDao() throws SQLException {
        if(festivalCountrysDataDao == null){
            festivalCountrysDataDao = getDao(FestivalCountrys.class);
        }
        return festivalCountrysDataDao;
    }

    public Dao<NotificationList, Integer> getNotificationListsDataDao() throws SQLException {
        if(notificationListsDataDao == null)
            notificationListsDataDao = getDao(NotificationList.class);

        return notificationListsDataDao;
    }
}
