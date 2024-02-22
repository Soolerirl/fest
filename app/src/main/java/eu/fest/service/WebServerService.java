package eu.fest.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;
import org.json.JSONException;

import java.security.cert.CertificateException;
import java.util.Map;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.fest.ServiceBusApplication;
import eu.fest.model.FriendItem;
import eu.fest.model.Model;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.model.databases.Events;
import eu.fest.model.databases.Festival;
import eu.fest.model.databases.Performers;
import eu.fest.model.databases.Pins;
import eu.fest.net.RESTWebService;

import eu.fest.net.Responses.UserDataResponse;
import eu.fest.net.Responses.UserResponse;
import eu.fest.net.RestResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;


@EBean(scope = EBean.Scope.Singleton)
public class WebServerService {

    @App
    ServiceBusApplication app;

    private RESTWebService service;

    private Callback<Model> generalCallbackHandler;

    public WebServerService() {

        Gson gson = new GsonBuilder().create();

        OkClient client = getOkClient();

        RestAdapter restAdapter = new RestAdapter.Builder().setClient(client)
                .setEndpoint(ServiceBusApplication.WEB_ENDPOINT_ADDRESS)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();

        System.setProperty("http.keepAlive", "false");

        service = restAdapter.create(RESTWebService.class);

        generalCallbackHandler = new Callback<Model>() {
            @Override
            public void success(Model s, Response response) {
                if (s != null) {
                    Log.d("REST RESPONSE", s.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                generalErrorHandler(error);
            }
        };
    }

    protected void generalErrorHandler(RetrofitError error) {
        if (error != null && error.getResponse() != null) {
            Log.d("REST", error.getResponse().getReason() + " | " + error.getResponse().getStatus());
        }
    }

    protected Map map(Map map) {
        map.put("device_key", app.getRegistrationId());
        map.put("device_type", app.getRegistrationDevice());
        return map;
    }

    /*public void loginWithEmail(User data, final Callback<CurrentUser> callback) {
        service.loginWithEmail(map(data.mapForLogin()), new Callback<RestResponse<CurrentUser>>() {
            @Override
            public void success(RestResponse<CurrentUser> userRestResponse, Response response) {
                CurrentUser currentUser = userRestResponse.getResponse();
                currentUser.setSuccess(userRestResponse.isSuccess());
                callback.success(currentUser, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }*/

    public void loginWithEmail(User data, final Callback<CurrentUser> callback) {
        service.loginWithEmail(map(data.mapForLogin()), new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser currentUser, Response response) {
                callback.success(currentUser, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getSettings(final Callback<CurrentUser> callback){
        service.getSettings(new Callback<UserResponse<CurrentUser>>() {
            @Override
            public void success(UserResponse<CurrentUser> currentUserRestResponse, Response response) {
                callback.success(currentUserRestResponse.getResponse().get(0), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loginWithFace(User data, final Callback<CurrentUser> callback) {
        service.loginWithFace(map(data.mapForFaceLogin()), new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void connectFace(User data, final Callback<User> callback) {
        service.connectFace(map(data.mapForFaceLogin()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void loginWithTwitter(User data, final Callback<CurrentUser> callback) {
        service.loginWithTwitter(map(data.mapForTwitterLogin()), new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void connectTwitter(User data, final Callback<User> callback) {
        service.connectTwitter(map(data.mapForTwitterLogin()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void changeUserData(CurrentUser data, final Callback<CurrentUser> callback){
        service.changeUserData(map(data.mapForChangeData()), new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void changePassword(User data, final Callback<User> callback){
        service.changePassword(data.mapForNewPassword(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void changeEmail(User data, final Callback<User> callback){
        service.changeEmail(map(data.mapForLostPassword()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void lostPasswordReset(User data, final Callback<User> callback) {
        service.lostPasswordReset(data.mapForLostPassword(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void registration(User data, Callback<User> callback) {
        service.registration(map(data.mapForRegister()), callback);
    }

    public void logout(User data, final Callback<User> callback){
        service.logout(map(data.mapForRememberMe()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void sendSettings(CurrentUser data, final Callback<CurrentUser> callback){
        service.sendSettings(map(data.mapforSettings()), callback);
    }

    public void getFestivalList(final Callback<Festival> callback){
        service.getFestivalList(new Callback<RestResponse<Festival>>() {
            @Override
            public void success(RestResponse<Festival> festivalRestResponse, Response response) {
                Log.d("asd", String.valueOf(festivalRestResponse.getResponse()));
                callback.success(festivalRestResponse.getResponse(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getEventList(String festivalId, final Callback<Events> callback){
        service.getEventList(festivalId, new Callback<Events>() {
            @Override
            public void success(Events events, Response response) {
                callback.success(events, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addFavoriteFestival(String festivalId, final Callback<Festival> callback){
        service.addFavoriteFestival(festivalId, new Callback<Festival>() {
            @Override
            public void success(Festival festival, Response response) {
                callback.success(festival, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteFavoriteFestival(String festivalId, final Callback<Festival> callback){
        service.deleteFavoriteFestival(festivalId, new Callback<Festival>() {
            @Override
            public void success(Festival festival, Response response) {
                callback.success(festival, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addWatchFestivalEvent(String festivalId,String festivaleventId, final Callback<Events> callback){
        service.addWatchFestivalEvent(festivalId, festivaleventId, new Callback<Events>() {
            @Override
            public void success(Events event, Response response) {
                callback.success(event, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteWatchFestivalEvent(String festivalId,String festivaleventId, final Callback<Events> callback){
        service.deleteWatchFestivalEvent(festivalId, festivaleventId, new Callback<Events>() {
            @Override
            public void success(Events event, Response response) {
                callback.success(event, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addGoingFestivalEvent(String festivalId,String festivaleventId, final Callback<Events> callback){
        service.addGoingFestivalEvent(festivalId, festivaleventId, new Callback<Events>() {
            @Override
            public void success(Events event, Response response) {
                callback.success(event, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteGoingFestivalEvent(String festivalId,String festivaleventId, final Callback<Events> callback){
        service.deleteGoingFestivalEvent(festivalId, festivaleventId, new Callback<Events>() {
            @Override
            public void success(Events event, Response response) {
                callback.success(event, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getPerformersList(String festivalId, String festivaleventId, final Callback<Performers> callback){
        service.getPerformersList(festivalId, festivaleventId, new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                callback.success(performers, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void sendCrash(Map<String, String> crash, final Callback<User> callback){
        service.sendCrash(map(crash), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addFriend(FriendItem friend, final Callback<User> callback){
        service.addFriend(map(friend.mapfriendRequest()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteFriend(FriendItem friend, final Callback<User> callback){
        service.deleteFriend(map(friend.mapfriendRequest()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void fileUpload(TypedFile typedFile, final Callback<User> callback){
        service.fileUpload(typedFile, app.getRegistrationId(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void searchFriends(String current, String searchPhrase, final Callback<FriendItem> callback){
        service.searchFriends(current, searchPhrase, new Callback<FriendItem>() {
            @Override
            public void success(FriendItem friendItem, Response response) {
                callback.success(friendItem, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getFriendRequests(final Callback<FriendItem> callback){
        service.getFriendRequests(new Callback<FriendItem>() {
            @Override
            public void success(FriendItem friendItem, Response response) {
                callback.success(friendItem, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserProfile(String userId, final Callback<FriendItem> callback){
        service.getUserProfile(userId, new Callback<RestResponse<FriendItem>>() {
            @Override
            public void success(RestResponse<FriendItem> userDataResponseRestResponse, Response response) {
                callback.success(userDataResponseRestResponse.getResponse(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void rateFestivalEvent(Events event, final Callback<Events> callback){
        service.rateFestivalEvent(event.getFestivalId() + "", event.getServerId() + "", map(event.mapForRating()), new Callback<Events>() {
            @Override
            public void success(Events events, Response response) {
                callback.success(events, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addLocation(Events event, Pins pins, final Callback<Pins> callback){

        service.addLocation(event.getFestivalId() + "", event.getServerId() + "", map(pins.createPin()), pins.getAddedUsersId(),new Callback<Pins>() {
        //service.addLocation(event.getFestivalId() + "", event.getServerId() + "", pins,new Callback<Pins>() {
            @Override
            public void success(Pins pins, Response response) {
                callback.success(pins, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void changeLocation(Events event, Pins pins, final Callback<Pins> callback){
        service.changeLocation(event.getFestivalId() + "", event.getServerId() + "", pins.getServer_id()+"", pins.createPin(), new Callback<Pins>() {
            @Override
            public void success(Pins pins, Response response) {
                callback.success(pins, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteLocation(Events event, Pins pins, final Callback<Pins> callback){
        service.deleteLocation(event.getFestivalId() + "", event.getServerId() + "", pins.getServer_id()+"", pins.createPin(), new Callback<Pins>() {
            @Override
            public void success(Pins pins, Response response) {
                callback.success(pins, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getLocation(Events events, final Callback<Pins> callback){
        service.getLocation(events.getFestivalId() + "", events.getServerId() + "", new Callback<RestResponse<Pins>>() {
            @Override
            public void success(RestResponse<Pins> pinsRestResponse, Response response) {
                callback.success(pinsRestResponse.getResponse(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void addGoingPerformers(Performers performers, final Callback<Performers> callback){
        service.addGoingPerformers(performers.getFestivalId()+"", performers.getEventId()+"", performers.getServerId()+"", new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                callback.success(performers, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void deleteGoingPerformers(Performers performers, final Callback<Performers> callback){
        service.deleteGoingPerformers(performers.getFestivalId()+"", performers.getEventId()+"", performers.getServerId()+"", new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                callback.success(performers, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getNotific(final Callback<Performers> callback){
        service.getNotific(new Callback<Performers>() {
            @Override
            public void success(Performers performers, Response response) {
                callback.success(performers, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }


/////////////////////////////sslcheck////////////////////////////////////////////////////////////
    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equalsIgnoreCase("backend.festival4ever.com")) {
                    //if (hostname.equalsIgnoreCase("192.168.0.100")) {
                        Log.d("RestUtilImpl", "Approving certificate for " + hostname);
                        return true;
                    }else {
                        return false;
                    }
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static OkClient getOkClient (){
        OkHttpClient client1 = getUnsafeOkHttpClient();
        OkClient _client = new OkClient(client1);
        return _client;
    }
}


