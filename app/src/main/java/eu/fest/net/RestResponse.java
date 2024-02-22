package eu.fest.net;

import eu.fest.model.Model;

public class RestResponse<E> extends Model {

    private E message;

//    private String message;

    private int count;

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public E getResponse() {
        return message;
    }

    public void setResponse(E message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}