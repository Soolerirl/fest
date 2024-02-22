package eu.fest.net.Responses;

import java.util.List;

import eu.fest.model.Model;

public class UserResponse<E>extends Model {

    private List<E> message;

    private int count;

    public List<E> getResponse() {
        return message;
    }

    public void setResponse(List<E> message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
