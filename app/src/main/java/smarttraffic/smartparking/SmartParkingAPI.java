package smarttraffic.smartparking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import smarttraffic.smartparking.apiFeed.LoginFeed;
import smarttraffic.smartparking.apiFeed.LogoutFeed;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.dataModels.ProfileRegistry;

public interface SmartParkingAPI {
    @POST("login")
    Call<LoginFeed> loginUser(@Body Credentials credentials);

    @POST("profiles/")
    Call<ProfileRegistry> signUpUser(@Body ProfileRegistry profileRegistry);

//    @POST("/logout")
//    Call<LogoutFeed> logoutUser(@Body Credentials credentials);
//
//    @POST("/logout")
//    Call<LogoutFeed> configProfile(@Body Credentials credentials);

    /*
    * Here should be all the API Rest services that the mobile app
    * consumes from the SmartTraffic server...
    * */

}
