package smarttraffic.smartparking;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import smarttraffic.smartparking.dataModels.Passwords;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.ResetPassword;

public interface SmartParkingAPI {


    /**
     * Login, Logout user session and a Service to know if user is or not Logged
     * and get the CSRF token...
     * **/
    @Multipart
    @POST("loggin/")
    Call<ProfileUser> logginUser(@Part("username") RequestBody username,
                                 @Part("password") RequestBody password);

    @GET("isUserLogged/")
    Call<ResponseBody> isUserLogged();

    @GET("logout/")
    Call<ResponseBody> logoutUser();

    @POST("changePass/")
    Call<ResponseBody> changeUserPassword(@Body Passwords passwords);

    /**
     * UPDATE USER PROFILE INFO
     * **/

    @PUT("users/{identifier}/")
    Call<ResponseBody> updateUserProfile(@Path("identifier") Integer userId, @Body ProfileRegistry newProfile);

    /**
     * LIST OF ALL USERS PROFILE
     * **/

    @GET("users/")
    Call<ProfileUser> usersList();

    /**
     * GET USER PROFILE INFORMATION
     * **/

    @GET("users/{identifier}/")
    Call<ProfileUser> userProfile(@Path("identifier") Integer identifier);

    /**
     * REGISTRATION OF USER
     **/

    @POST("users/")
    Call<ProfileUser> signUpUser(@Body ProfileRegistry profileRegistry);

    /**
     * HERE THE CONSULT IF USER, WITH DATA FOR RECOVER PASS
     **/

    @POST("isReseteable/")
    Call<ResponseBody> canResetPass(@Body ResetPassword resetPassword);

}
