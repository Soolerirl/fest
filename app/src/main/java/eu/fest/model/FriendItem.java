package eu.fest.model;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class FriendItem extends Model {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("friend_status_code")
    private int yourfriend;

    public FriendItem(){}

    public FriendItem(int user_id, String firstName, String lastName, String profileImage, int yourfriend){
        this.user_id = user_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.yourfriend = yourfriend;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getYourfriend() {
        return yourfriend;
    }

    public void setYourfriend(int yourfriend) {
        this.yourfriend = yourfriend;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Map<String, String> mapfriendRequest() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("user_id",user_id+"");

        return map;
    }
}
