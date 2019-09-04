package smarttraffic.smartparking;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import smarttraffic.smartparking.activities.ChangePasswordActivity;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.SmartParkingLot;
import smarttraffic.smartparking.dataModels.SmartParkingSpot;
import smarttraffic.smartparking.dataModels.UserToken;

public interface SmartParkingAPI {

    @POST("auth-token/")
    Call<UserToken> getUserToken(@Body Credentials userCredentials);

    @POST("users/")
    Call<ProfileUser> signUpUser(@Body ProfileRegistry profileRegistry);

    @PATCH("users/{identifier}/")
    Call<ProfileUser> updateUserProfile(@Path("identifier") Integer userId,
                                        @Body ChangePasswordActivity.Password newProfile);

    @GET("spots_of/{lotName}/")
    Call<List<SmartParkingSpot>> getAllSpotsInLot(@Path("lotName") String lotName);

    @GET("lots/")
    Call<List<SmartParkingLot>> getAllLots();

    @PUT("spots/{lotId}")
    Call<SmartParkingSpot> updateSpot(@Body SmartParkingSpot updated, Integer lotId);

}
