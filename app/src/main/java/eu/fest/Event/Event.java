package eu.fest.Event;


public abstract class Event {
    private Object data;

    public Event() {

    }

    public Event(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
