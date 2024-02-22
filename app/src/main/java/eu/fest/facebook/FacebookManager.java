package eu.fest.facebook;

import android.app.Activity;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;

import java.util.Arrays;
import java.util.List;

public class FacebookManager {

    private Activity activity;

    private Session.StatusCallback statusCallback;

    private List<String> permissions = Arrays.asList("email", "user_birthday", "user_relationships", "user_location", "user_friends");

    public FacebookManager(Activity activity) {
        this.activity = activity;
        Session session = new Session(activity);
        Session.setActiveSession(session);
    }

    public void runQuery(final FacebookSessionCallback callback) {
        Log.d("FB___", "Run query");
        statusCallback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d("FB___", "Status callback with " + state.toString());
                if (state.equals(SessionState.OPENED)) {
                    Log.d("FB___", "Callback runned in session: " + session.toString());
                    callback.runWithActiveSession(session);
                }
                Log.d("FB____","");
            }
        };

        Session session = Session.getActiveSession();

        if (!session.isOpened() && !session.isClosed()) {
            Log.d("FB___", "!!2!! Open for read with permisions starting...");
            session.openForRead(new Session.OpenRequest(activity).setPermissions(permissions).setCallback(statusCallback));
            Session.setActiveSession(session);
        } else {
            Log.d("FB___", "Open active session.");
            Session.openActiveSession(activity, true, statusCallback);
        }

    }

    public void me(final Request.GraphUserCallback graphUserCallback) {
        runQuery(new FacebookSessionCallback() {
            @Override
            public void runWithActiveSession(Session session) {
                Log.d("FB___", "Execute Me request with session:" + session.toString());
                Request.executeMeRequestAsync(session, graphUserCallback);

            }

        });
    }

    public Session getActiveSession() {
        return Session.getActiveSession();
    }
}