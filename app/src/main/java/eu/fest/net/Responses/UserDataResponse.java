package eu.fest.net.Responses;

import eu.fest.model.Model;

public class UserDataResponse<E>extends Model {

    private E user_data;

    private int count;

    public E getUser_data() {
        return user_data;
    }

    public void setUser_data(E user_data) {
        this.user_data = user_data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
