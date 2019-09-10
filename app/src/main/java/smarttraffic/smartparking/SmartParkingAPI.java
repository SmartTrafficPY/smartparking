package smarttraffic.smartparking;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import smarttraffic.smartparking.activities.ChangePasswordActivity;
import smarttraffic.smartparking.dataModels.Credentials;
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

    @POST("auth-token/")
    Call<UserToken> getUserToken(@Body Credentials userCredentials);

    @POST("users/")
    Call<ProfileUser> signUpUser(@Body ProfileRegistry profileRegistry);

    @PATCH("users/{identifier}/")
    Call<ProfileUser> updateUserProfile(@Path("identifier") Integer userId,
                                        @Body ChangePasswordActivity.Password newProfile);

    /**SPOTS**/

    @POST("spots/{spotId}/reset/")
    Call<ResponseBody> resetFreeSpot(@Path("spotId") Integer spotId);

    @POST("spots/{spotId}/set/")
    Call<ResponseBody> setOccupiedSpot(@Path("spotId") Integer spotId);

    @POST("spots/nearby/")
    Call<HashMap<String, String>> getMapNearbySpots(@Body NearbyLocation nearbyLocation);

    @POST("spots/nearby/")
    Call<SpotList> getGeoJsonNearbySpots(@Body NearbyLocation nearbyLocation);

    @GET("spots/")
    Call<SpotList> getAllSpots();

    @GET("spots/{spotId}/")
    Call<Spot> getASpot(@Path("spotId") Integer spotId);

    /**LOTS**/

    @GET("lots/")
    Call<LotList> getAllLots();

    @GET("lots/{lotId}/")
    Call<Lot> getALot(@Path("lotId") Integer lotId);

    @GET("lots/{lotId}/spots/")
    Call<HashMap<String, String>> getAllMapSpotsInLot(@Path("lotId") Integer lotId);

    @GET("lots/{lotId}/spots/")
    Call<SpotList> getAllGeoJsonSpotsInLot(@Path("lotId") Integer lotId);
}
