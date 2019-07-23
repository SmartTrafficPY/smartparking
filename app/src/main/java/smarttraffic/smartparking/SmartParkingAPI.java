package smarttraffic.smartparking;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import smarttraffic.smartparking.dataModels.ProfileRegistry;

public interface SmartParkingAPI {

    /**
     * Login, Logout user session and a Service to know if user is or not Logged
     * and get the CSRF token...
     **/

    @Multipart
    @POST("login/")
    Call<ResponseBody> loginUser(@Part("username") RequestBody username,
                                 @Part("password") RequestBody password);

    @Multipart
    @POST("loggin/")
    Call<ResponseBody> logginUser(@Part("username") RequestBody username,
                                 @Part("password") RequestBody password);

    @GET("isUserLogged/")
    Call<ResponseBody> isUserLogged();

    @GET("logout/")
    Call<ResponseBody> logoutUser();

    /**
     * LIST OF ALL USERS PROFILE
     * **/

    @GET("users/")
    Call<ResponseBody> usersList();

    @POST("users/")
    Call<ProfileRegistry> signUpUser(@Body ProfileRegistry profileRegistry);

}
