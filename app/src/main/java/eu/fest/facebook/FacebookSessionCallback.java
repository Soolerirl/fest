package eu.fest.facebook;

import com.facebook.Session;

public interface FacebookSessionCallback {

    void runWithActiveSession(Session session);
}
