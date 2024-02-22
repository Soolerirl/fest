package eu.fest.Event;

import eu.fest.model.PushNotification;

public class PushNotificationReceivedEvent extends Event{

    public PushNotificationReceivedEvent(PushNotification data) {
        this.setData(data);
    }

    @Override
    public PushNotification getData() {
        return (PushNotification) super.getData();
    }
}

