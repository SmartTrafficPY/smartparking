package smarttraffic.smartparking;

import java.util.List;

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
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.SmartParkingLot;
import smarttraffic.smartparking.dataModels.SmartParkingSpot;

public interface SmartParkingAPI {

    @Multipart
    @POST("loggin/")
    Call<ProfileUser> logginUser(@Part("username") RequestBody username,
                                 @Part("password") RequestBody password);

    @GET("isUserLogged/")
    Call<ResponseBody> isUserLogged();

    @GET("logout/")
    Call<ResponseBody> logoutUser();

    @PUT("users/{identifier}/")
    Call<ResponseBody> updateUserProfile(@Path("identifier") Integer userId, @Body ProfileRegistry newProfile);


    @GET("users/")
    Call<ProfileUser> usersList();

    @GET("users/{identifier}/")
    Call<ProfileUser> userProfile(@Path("identifier") Integer identifier);

    @POST("users/")
    Call<ProfileUser> signUpUser(@Body ProfileRegistry profileRegistry);

    @GET("spots_of/{lotName}/")
    Call<List<SmartParkingSpot>> getAllSpotsInLot(@Path("lotName") String lotName);

    @GET("lots/")
    Call<List<SmartParkingLot>> getAllLots();

    @PUT("spots/{lotId}")
    Call<SmartParkingSpot> updateSpot(@Body SmartParkingSpot updated, Integer lotId);

}
