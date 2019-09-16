package smarttraffic.smartparking;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import smarttraffic.smartparking.activities.ChangePasswordActivity;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.dataModels.Events;
import smarttraffic.smartparking.dataModels.Lots.Lot;
import smarttraffic.smartparking.dataModels.Lots.LotList;
import smarttraffic.smartparking.dataModels.NearbyLocation;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.Spots.Spot;
import smarttraffic.smartparking.dataModels.Spots.SpotList;
import smarttraffic.smartparking.dataModels.UserToken;

public interface SmartParkingAPI {

    /**USERS**/

    @POST("smartparking/auth-token/")
    Call<UserToken> getUserToken(@Body Credentials userCredentials);

    @POST("smartparking/users/")
    Call<ProfileUser> signUpUser(@Body ProfileRegistry profileRegistry);

    @PATCH("smartparking/users/{identifier}/")
    Call<ProfileUser> updateUserProfile(@Path("identifier") Integer userId,
                                        @Body ChangePasswordActivity.Password newProfile);

    /**SPOTS**/

    @POST("smartparking/spots/{spotId}/reset/")
    Call<ResponseBody> resetFreeSpot(@Path("spotId") Integer spotId);

    @POST("smartparking/spots/{spotId}/set/")
    Call<ResponseBody> setOccupiedSpot(@Path("spotId") Integer spotId);

    @POST("smartparking/spots/nearby/")
    Call<HashMap<String, String>> getMapNearbySpots(@Body NearbyLocation nearbyLocation);

    @POST("smartparking/spots/nearby/")
    Call<SpotList> getGeoJsonNearbySpots(@Body NearbyLocation nearbyLocation);

    @GET("smartparking/spots/")
    Call<SpotList> getAllSpots();

    @GET("smartparking/spots/{spotId}/")
    Call<Spot> getASpot(@Path("spotId") Integer spotId);

    /**LOTS**/

    @GET("smartparking/lots/")
    Call<LotList> getAllLots();

    @GET("smartparking/lots/{lotId}/")
    Call<Lot> getALot(@Path("lotId") Integer lotId);

    @GET("smartparking/lots/{lotId}/spots/")
    Call<HashMap<String, String>> getAllMapSpotsInLot(@Path("lotId") Integer lotId);

    @GET("smartparking/lots/{lotId}/spots/")
    Call<SpotList> getAllGeoJsonSpotsInLot(@Path("lotId") Integer lotId);

    /**EVENTS**/

    @POST("events/")
    Call<ResponseBody> setUserEvent(@Header("Content-Type") String content_type, @Body Events event);

}
