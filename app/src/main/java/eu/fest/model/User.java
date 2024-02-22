package eu.fest.model;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class User extends Model {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("facebook_id")
    private String facebookId;

    @SerializedName("success")
    private String success;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("profile_image")
    private String profileImage;

    private String email;

    private String gender;

    private String password;

    private String oldpassword;

    private String passwordagain;

    private String deviceId;

    private String device_type;

    private String access_token;

    private String oauth_token;

    private String oauth_verifier;

    private boolean connect_social = false;

    private String BirthDate;

    public User(){

    }

    public User(String facebookId, String firstName, String lastName){
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordAgain() {
        return passwordagain;
    }

    public void setPasswordAgain(String passwordagain) {
        this.passwordagain = passwordagain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFacebookProfileUrl() {
        if (this.facebookId != null) {
            return "https://graph.facebook.com/" + getFacebookId() + "/picture?type=large";
        } else {
            return this.profilePictureUrl;
        }
    }

    public void setAccess_token(String access_token){
        this.access_token = access_token;
    }

    public String getAccess_token(){
        return this.access_token;
    }

    public void setConnect_social(boolean connect_social){
        this.connect_social = connect_social;
    }

    public boolean getConnect_social(){
        return this.connect_social;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOauth_token() {
        return oauth_token;
    }

    public void setOauth_token(String oauth_token) {
        this.oauth_token = oauth_token;
    }

    public String getOauth_verifier() {
        return oauth_verifier;
    }

    public void setOauth_verifier(String oauth_verifier) {
        this.oauth_verifier = oauth_verifier;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public boolean isSuccess(){
        if(this.success.equals("false")){
            return false;
        }else{
            return true;
        }
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setPasswordagain(String passwordagain) {
        this.passwordagain = passwordagain;
    }

    public Map<String, String> mapForLogin() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("email", email);
        map.put("password", password);
        map.put("device_key", deviceId);
        map.put("device_type",device_type);

        return map;
    }

    public Map<String, String> mapForFaceLogin() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("access_token", access_token);
        map.put("device_key", deviceId);
        map.put("device_type",device_type);

        return map;
    }

    public Map<String, String> mapForTwitterLogin() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("oauth_token", oauth_token);
        map.put("oauth_verifier", oauth_verifier);
        map.put("device_key", deviceId);
        map.put("device_type",device_type);

        return map;
    }

    public Map<String, String> mapForRegister() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("first_name",firstName);
        map.put("last_name",lastName);
        map.put("gender",gender);
        map.put("birthdate",BirthDate);
        map.put("email", email);
        map.put("password", password);
        map.put("password_again", passwordagain);

        return map;
    }

    public Map<String, String> mapForChangeData() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("first_name",firstName);
        map.put("last_name",lastName);
        map.put("gender",gender);
        map.put("birthdate",BirthDate);

        return map;
    }

    public Map<String, String> mapForLostPassword() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("email", email);

        return map;
    }

    public Map<String, String> mapForRememberMe(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("device_key", deviceId);

        return map;
    }

    public Map<String, String> mapForNewPassword(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("device_key", deviceId);
        map.put("old_password", oldpassword);
        map.put("new_password", password);
        map.put("new_password_again", passwordagain);

        return map;
    }
}
