package smarttraffic.smartparking.receivers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.Interceptors.AddUserTokenInterceptor;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.activities.HomeActivity;
import smarttraffic.smartparking.dataModels.Lots.Lot;
import smarttraffic.smartparking.dataModels.Lots.LotList;
import smarttraffic.smartparking.dataModels.Lots.LotProperties;
import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.receivers
 */

public class AddAlarmReceiver extends BroadcastReceiver {

    private GeofencingClient geofencingClient;
    private PendingIntent mGeofencePendingIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        if(!Utils.getGeofenceStatus(context)){
            addParkingLotsGeofences(context);
        }
    }

    private void addParkingLotsGeofences(final Context context) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddUserTokenInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<LotList> call = smartParkingAPI.getAllLots();

        call.enqueue(new Callback<LotList>() {
            @Override
            public void onResponse(Call<LotList> call, Response<LotList> response) {
                switch (response.code()) {
                    case 200:
                        List<Lot> lots = response.body().getFeatures();
                        Utils.saveLotInSharedPreferences(context, lots);
                        ArrayList<Geofence> geofenceList = new ArrayList<>();
                        for (Lot lot : lots) {
                            LotProperties properties = lot.getProperties();
                            Point center = properties.getCenter().getPointCoordinates();
                            geofenceList.add(generateGeofence(center.getLatitud(),
                                    center.getLongitud(),
                                    properties.getRadio(),
                                    properties.getName(), false));
                        }
                        addGeofences(context, geofenceList);
                        Utils.saveListOfGateways(context, response.body());
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<LotList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private Geofence generateGeofence(double latitude, double longitud, float radius, String nameId,
                                      boolean isSpotGeofence) {
        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(nameId)
                .setCircularRegion(
                        latitude,
                        longitud,
                        radius
                )
                .setLoiteringDelay(1000 * 60 * 10)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_DWELL);
        if(isSpotGeofence){
            builder.setExpirationDuration(Constants.getHoursInMilliseconds() * 24);
        }else{
            builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
        }
        Geofence geofence = builder.build();
        return geofence;
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences(Context context, ArrayList<Geofence> geofenceArrayList) {
        if (!checkPermissions(context)) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(geofenceArrayList), getGeofencePendingIntent(context));
        Utils.geofencesSetUp(context,true);
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions(Context context) {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent(Context context) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);

        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}
