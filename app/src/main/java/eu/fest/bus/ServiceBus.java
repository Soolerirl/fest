package eu.fest.bus;


import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.HashMap;

import eu.fest.Event.Event;

public class ServiceBus {
    private ServiceBus() {

    }

    private static Bus eventBus;

    private static HashMap<Integer, Object> objectMap = null;


    public static void initialize() {
        ServiceBus.eventBus = new Bus(ThreadEnforcer.ANY);
        objectMap = new HashMap<Integer, Object>();
    }

    public static void triggerEvent(Event event) {
        ServiceBus.eventBus.post(event);
    }


    public static void register(Object object) {
        if (!isRegistered(object)) {
            objectMap.put(identify(object), object);
            ServiceBus.eventBus.register(object);
        }
    }

    public static void unregister(Object object) {
        if (isRegistered(object)) {
            objectMap.remove(identify(object));
            ServiceBus.eventBus.unregister(object);
        }
    }

    public static boolean isRegistered(Object object) {
        return objectMap.containsKey(identify(object));
    }

    protected static Integer identify(Object object) {
        return Integer.valueOf(java.lang.System.identityHashCode(object));
    }
}
