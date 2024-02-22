package eu.fest.net;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

import eu.fest.model.FriendItem;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.model.databases.Performers;
import eu.fest.model.databases.Pins;
import eu.fest.net.Responses.UserDataResponse;
import eu.fest.net.Responses.UserResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

public interface RESTWebService {

    /*@FormUrlEncoded
    @POST("/api/v1/sign-in")
    void loginWithEmail(@FieldMap Map<String, String> params, Callback<RestResponse<CurrentUser>> callback);*/

    @FormUrlEncoded
    @POST("/api/v1/sign-in")
    void loginWithEmail(@FieldMap Map<String, String> params, Callback<CurrentUser> callback);

    @FormUrlEncoded
    @POST("/api/v1/facebook-sign-in")
    void loginWithFace(@FieldMap Map<String, String> params, Callback<CurrentUser> callback);

    @FormUrlEncoded
    @POST("/api/v1/connect-facebook")
    void connectFace(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/twitter-sign-in")
    void loginWithTwitter(@FieldMap Map<String, String> params, Callback<CurrentUser> callback);

    @FormUrlEncoded
    @POST("/api/v1/connect-twitter")
    void connectTwitter(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/sign-out")
    void logout(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/change-user-data")
    void changeUserData(@FieldMap Map<String, String> params, Callback<CurrentUser> callback);

    @FormUrlEncoded
    @POST("/api/v1/lost-password")
    void lostPasswordReset(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/change-password")
    void changePassword(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/change-email")
    void changeEmail(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/sign-up")
    void registration(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/save-settings")
    void sendSettings(@FieldMap Map<String, String> params, Callback<CurrentUser> callback);

    @GET("/api/v1/get-settings")
    void getSettings(Callback<UserResponse<CurrentUser>> callback);

    @FormUrlEncoded
    @POST("/api/v1/add-friend")
    void addFriend(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/delete-friend")
    void deleteFriend(@FieldMap Map<String, String> params, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/v1/festivals/{festival_id}/events/{festival_event_id}/rating")
    void rateFestivalEvent(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @FieldMap Map<String, String> params, Callback<Events> callback);

    @FormUrlEncoded
    @POST("/api/v1/festivals/{festival_id}/events/{festival_event_id}/add-location")
    void addLocation(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @FieldMap Map<String, Object> params, @Field("user_id[]") ArrayList<Integer> user_id, Callback<Pins> callback);
    //void addLocation(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @Body Pins pins, Callback<Pins> callback);

    @FormUrlEncoded
    @POST("/api/v1/festivals/{festival_id}/events/{festival_event_id}/locations/{festival_event_location_id}/change-location")
    void changeLocation(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @Path("festival_event_location_id") String festivalEventLocationId, @FieldMap Map<String, Object> params, Callback<Pins> callback);

    @FormUrlEncoded
    @POST("/api/v1/festivals/{festival_id}/events/{festival_event_id}/locations/{festival_event_location_id}/delete-location")
    void deleteLocation(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @Path("festival_event_location_id") String festivalEventLocationId, @FieldMap Map<String, Object> params, Callback<Pins> callback);

    @GET("/api/v1/get-user-going-events-events")
    void getNotific( Callback<Performers> callback);

    @GET("/api/v1/get-friends")
    void getFriendRequests( Callback<FriendItem> callback);

    @GET("/api/v1/festivals")
    void getFestivalList( Callback<RestResponse<Festival>> callback);

    @GET("/api/v1/festivals/{festival_id}")
    void getEventList(@Path("festival_id") String festivalId, Callback<Events> callback);

    @GET("/api/v1/festivals/{festival_id}/add-favorite")
    void addFavoriteFestival(@Path("festival_id") String festivalId, Callback<Festival> callback);

    @GET("/api/v1/festivals/{festival_id}/delete-favorite")
    void deleteFavoriteFestival(@Path("festival_id") String festivalId, Callback<Festival> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/add-watch")
    void addWatchFestivalEvent(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<Events> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/delete-watch")
    void deleteWatchFestivalEvent(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<Events> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/add-going")
    void addGoingFestivalEvent(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<Events> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/delete-going")
    void deleteGoingFestivalEvent(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<Events> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/{performers_id}/add-going")
    void addGoingPerformers(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @Path("performers_id") String performersId, Callback<Performers> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/{performers_id}/delete-going")
    void deleteGoingPerformers(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, @Path("performers_id") String performersId, Callback<Performers> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/performers")
    void getPerformersList(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<Performers> callback);

    @GET("/api/v1/users/{user_id}")
    void getUserProfile(@Path("user_id") String userId, Callback<RestResponse<FriendItem>> callback);

    @GET("/api/v1/festivals/{festival_id}/events/{festival_event_id}/locations")
    void getLocation(@Path("festival_id") String festivalId, @Path("festival_event_id") String festivalEventId, Callback<RestResponse<Pins>> callback);

    @GET("/api/v1/users/search-user?")
    void searchFriends(@Query("current") String festivalId, @Query("searchPhrase") String festivalEventId, Callback<FriendItem> callback);

    @FormUrlEncoded
    @POST("/api/v1/crash-report")
    void sendCrash(@FieldMap Map<String, String> params, Callback<User> callback);

    @Multipart
    @POST("/api/v1/change-profile-image")
    void fileUpload(@Part("profile_image") TypedFile file, @Part("device_key") String description, Callback<User> callback);

}
