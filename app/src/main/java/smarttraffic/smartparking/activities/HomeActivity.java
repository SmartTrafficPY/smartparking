package smarttraffic.smartparking.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.BuildConfig;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.Interceptors.AddGeoJsonInterceptor;
import smarttraffic.smartparking.Interceptors.AddUserTokenInterceptor;
import smarttraffic.smartparking.Interceptors.ReceivedTimeStampInterceptor;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.SmartParkingInitialData;
import smarttraffic.smartparking.StatesEnumerations;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.dataModels.Lots.Lot;
import smarttraffic.smartparking.dataModels.Lots.LotList;
import smarttraffic.smartparking.dataModels.Lots.LotProperties;
import smarttraffic.smartparking.dataModels.Lots.PointGeometry;
import smarttraffic.smartparking.dataModels.Spots.NearbySpot.NearbyLocation;
import smarttraffic.smartparking.dataModels.Point;
import smarttraffic.smartparking.dataModels.Spots.NearbySpot.NearbyPropertiesFeed;
import smarttraffic.smartparking.dataModels.Spots.NearbySpot.NearbySpot;
import smarttraffic.smartparking.dataModels.Spots.NearbySpot.NearbySpotList;
import smarttraffic.smartparking.dataModels.Spots.Spot;
import smarttraffic.smartparking.dataModels.Spots.SpotList;
import smarttraffic.smartparking.dataModels.Spots.SpotProperties;
import smarttraffic.smartparking.receivers.GeofenceBroadcastReceiver;
import smarttraffic.smartparking.services.DetectedActivitiesService;
import smarttraffic.smartparking.services.GeofenceTransitionsJobIntentService;

import static smarttraffic.smartparking.Interceptors.ReceivedTimeStampInterceptor.X_TIMESTAMP;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class HomeActivity extends AppCompatActivity {

    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.mapFragment)
    MapView mapView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.buttonRecenter)
    ImageButton buttonRecenter;
    @BindView(R.id.buttonAbout)
    ImageButton buttonAbout;

    private FusedLocationProviderClient mFusedLocationClient;
    private ActivityRecognitionClient mActivityRecognitionClient;

    private SettingsClient mSettingsClient;
    int activityTransition;
    int geofenceTransition;
    int confidence;
    boolean userNotResponse = true;
    boolean dialogSendAllready = false;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private List<Spot> spots = new ArrayList<Spot>();
    private ArrayList<String> geofencesTrigger = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver geofenceReceiver;
    private GeofencingClient geofencingClient;
    private PendingIntent mGeofencePendingIntent;
    //MAP...
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);

        mSettingsClient = LocationServices.getSettingsClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);

        setMapView();

        createLocationCallback();
        createLocationRequest(Constants.getSecondsInMilliseconds() * 2,
                Constants.getSecondsInMilliseconds());
        buildLocationSettingsRequest();

        addParkingLotsGeofences();

        buttonRecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationOverlay != null){
                    mapView.getController().setCenter(mLocationOverlay.getMyLocation());
                }
            }
        });

//        buttonAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
//                startActivity(intent);
//            }
//        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_TRANSITION_ACTIVITY_INTENT)) {
                    activityTransition = intent.getIntExtra(Constants.ACTIVITY_TYPE_TRANSITION, -1);
                    confidence = intent.getIntExtra(Constants.ACTIVITY_CONFIDENCE_TRANSITION, -1);
                }
            }
        };





        geofenceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.getBroadcastGeofenceTriggerIntent())) {
                    geofencesTrigger = intent.getStringArrayListExtra(
                            GeofenceTransitionsJobIntentService.GEOFENCE_TRIGGED);
                    geofenceTransition = intent.getIntExtra(
                            GeofenceTransitionsJobIntentService.TRANSITION,
                            -1);
                    managerOfTransitions();
                }
            }
        };
        setSupportActionBar(toolbar);
    }

    private void setMapView() {

        final String basic =
                "Basic " + Base64.encodeToString(SmartParkingInitialData.getCredentials().getBytes(), Base64.NO_WRAP);
        final Map<String, String> AuthHeader = new HashMap<>();
        AuthHeader.put("Authorization", basic);
        SharedPreferences preferencesManager = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferencesManager.edit();
        for (final Map.Entry<String, String> entry : AuthHeader.entrySet()) {
            final String key = "osmdroid.additionalHttpRequestProperty." + entry.getKey();
            editor.putString(key, entry.getValue()).apply();
        }

        editor.commit();

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(
                this));

        mapView.setTileSource(new OnlineTileSourceBase("SMARTPARKING CartoDB",
                16, 22, 256, ".png",
                new String[]{Constants.TILE_SERVER}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });


        IMapController mapController = mapView.getController();

        setGralMapConfiguration(mapController);
        //scale bar
        setScaleBar();

        setLocationOverlay();

        setCompassGestureOverlays();
//        setMarkersOnMap();
        //add all overlays
        addOverlays();
    }

    private void setMarkersOnMap() {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        OverlayItem overlayItem = new OverlayItem("Title1", "Description",
                new GeoPoint(-25.30604186, -57.59168641));
//        overlayItem.setMarker(getDrawable(R.drawable.about_menu));
        items.add(overlayItem);
        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
        mOverlay.setFocusItemsOnTap(true);

        mapView.getOverlays().add(mOverlay);
    }

    private void addOverlays() {
        mapView.getOverlays().add(mRotationGestureOverlay);
        mapView.getOverlays().add(mCompassOverlay);
        mapView.getOverlays().add(mLocationOverlay);
        mapView.getOverlays().add(mScaleBarOverlay);
    }

    private void setGralMapConfiguration(IMapController mapController) {
        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setFlingEnabled(true);
        mapController.setZoom(17);
    }

    private void setScaleBar() {
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
    }

    private void setLocationOverlay() {
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
    }

    private void setCompassGestureOverlays() {
        //add compass
        mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        mCompassOverlay.enableCompass();

        //rotation gestures
        mRotationGestureOverlay = new RotationGestureOverlay(this, mapView);
        mRotationGestureOverlay.setEnabled(true);
    }

    private void drawPolygon(List<GeoPoint> geoPoints, String status) {
        Polygon polygon = new Polygon();
        String color = "#C0C0C0";
        if (status.equals(StatesEnumerations.FREE.getEstado())) {
            color = "#00FF00";
        } else if (status.equals(StatesEnumerations.OCCUPIED.getEstado())) {
            color = "#FF0000";
        }
        polygon.setFillColor(Color.parseColor(color));
        polygon.setStrokeColor(Color.parseColor(color));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);
        mapView.getOverlayManager().add(polygon);
    }

    private void managerOfTransitions() {
        getSpotsFromGeofence(geofencesTrigger, false);
        final Handler handler = new Handler();
        final long delay = Constants.getMinutesInMilliseconds();
        Runnable cronJob = new Runnable() {
            public void run() {
                getSpotsFromGeofence(geofencesTrigger, true);
                handler.postDelayed(this, delay);
            }
        };
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.i(LOG_TAG, "Enter Transition");
                if (checkPermissions()) {
                    startLocationUpdates(mLocationRequest);
                } else if (!checkPermissions()) {
                    requestPermissions();
                }
                handler.postDelayed(cronJob, delay);
                requestActivityUpdates();
                Utils.setEntranceEvent(this, mCurrentLocation, "enter_lot");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.i(LOG_TAG, "Exit Transition");
                stopLocationUpdates();
                removeActivityUpdates();
                handler.removeCallbacks(cronJob);
                Utils.setEntranceEvent(this, mCurrentLocation,"exit_lot");
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.i(LOG_TAG, "Dwell Transition");
                break;
            default:
                Log.i(LOG_TAG, "No transition detected");
        }
    }

    private void getSpotsFromGeofence(ArrayList<String> geofencesTrigger, boolean isForUpdate) {
        if(geofencesTrigger != null) {
            if(isForUpdate){
                for (String geofenceTrigger : geofencesTrigger) {
                    updatesSpotsFromGeofence();
                }
            }else{
                for (String geofenceTrigger : geofencesTrigger) {
                    getSpotsGeographicValues(geofenceTrigger);
                }
            }
        }
    }

    private void getSpotsGeographicValues(String geofencesTrigger) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddGeoJsonInterceptor())
                .addInterceptor(new AddUserTokenInterceptor(this))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        int lotId = Utils.getLotInSharedPreferences(HomeActivity.this, geofencesTrigger);
        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<SpotList> call = smartParkingAPI.getAllGeoJsonSpotsInLot(lotId);

        call.enqueue(new Callback<SpotList>() {
            @Override
            public void onResponse(Call<SpotList> call, Response<SpotList> response) {
                switch (response.code()) {
                    case 200:
                        SpotList testSpots = response.body();
                        spots = testSpots.getFeatures();
                        if(spots != null){
                            for (Spot spot : spots) {
                                drawPolygon(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                            }
                        }
                        break;
                    default:
                        Toast.makeText(HomeActivity.this, "Por alguna razón no fue posible la conexión",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<SpotList> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    private void updatesSpotsFromGeofence() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                X_TIMESTAMP,MODE_PRIVATE);
        NearbyLocation nearbyLocation = new NearbyLocation();
        PointGeometry point = new PointGeometry();
        NearbyPropertiesFeed nearbyPropertiesFeed = new NearbyPropertiesFeed();

        if(mCurrentLocation != null){
            point.setPointCoordinates(mCurrentLocation);
        }
        nearbyPropertiesFeed.setPrevious_timestamp(sharedPreferences.getString(
                X_TIMESTAMP,"1559447999"));
        nearbyLocation.setGeometry(point);
        nearbyLocation.setProperties(nearbyPropertiesFeed);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedTimeStampInterceptor(this))
                .addInterceptor(new AddUserTokenInterceptor(this))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<NearbySpotList> call = smartParkingAPI.getGeoJsonNearbySpots("application/vnd.geo+json",
                "application/vnd.geo+json", nearbyLocation);

        call.enqueue(new Callback<NearbySpotList>() {
            @Override
            public void onResponse(Call<NearbySpotList> call, Response<NearbySpotList> response) {
                switch (response.code()) {
                    case 200:
                        List<NearbySpot> changedSpots = response.body().getFeatures();
                        if(changedSpots != null){
                            for (NearbySpot nearbySpot : changedSpots) {
                                drawPolygon(Utils.nearbySpotToListOfGeoPoints(nearbySpot),
                                        nearbySpot.getProperties().getState());
                            }
                        }
                        break;
                    default:
                        Toast.makeText(HomeActivity.this, "Por alguna razón no fue posible la conexión",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<NearbySpotList> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeActivityUpdates();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_TRANSITION_ACTIVITY_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceReceiver,
                new IntentFilter(Constants.getBroadcastGeofenceTriggerIntent()));
//        managerOfTransitions();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onCreate");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceReceiver);
        stopLocationUpdates();
        removeActivityUpdates();
        mapView.onPause();
    }

    private void addParkingLotsGeofences() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddUserTokenInterceptor(this))
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
                        Utils.saveLotInSharedPreferences(HomeActivity.this, lots);
                        ArrayList<Geofence> geofenceList = new ArrayList<>();
                        for (Lot lot : lots) {
                            LotProperties properties = lot.getProperties();
                            Point center = properties.getCenter().getPointCoordinates();
                            geofenceList.add(generateGeofence(center.getLatitud(),
                                    center.getLongitud(),
                                    properties.getRadio(),
                                    properties.getName(), false));
                        }
                        Toast.makeText(HomeActivity.this, "Se han agregado todos los geofences",
                                Toast.LENGTH_SHORT).show();
                        addGeofences(geofenceList);
                        break;
                    default:
                        Toast.makeText(HomeActivity.this, "Por problemas de conexion no " +
                                        "se han conseguido los predios del sistema",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<LotList> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG, t.toString());
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
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT);
        if(isSpotGeofence){
            builder.setExpirationDuration(Constants.getHoursInMilliseconds() * 24);
        }else{
            builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
        }
        Geofence geofence = builder.build();
        return geofence;
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(LOG_TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    Constants.getRequestPermissionsRequestCode());
                        }
                    });
        } else {
            Log.i(LOG_TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.getRequestPermissionsRequestCode());
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences(ArrayList<Geofence> geofenceArrayList) {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(geofenceArrayList), getGeofencePendingIntent());
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        geofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent,
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

    private GeofencingRequest getGeofenceRequest(Spot spot) {
        List<Point> points = spot.getGeometry().getPolygonPoints();
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(generateGeofence(points.get(0).getLatitud(), points.get(0).getLongitud(),
                15, "ParkinSpot" + spot.getProperties().getIdFromUrl(), true));
        return builder.build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionResult");
        if (requestCode == Constants.getRequestPermissionsRequestCode()) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(LOG_TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "Permission granted.");
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                    }
                });
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates(LocationRequest locationRequest) {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(LOG_TAG, "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(LOG_TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, Constants.REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(LOG_TAG, errorMessage);
                                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                checkForUserLocation(mCurrentLocation);
            }
        };
    }

    private void checkForUserLocation(Location mCurrentLocation) {
        int spotId = isPointInsideParkingSpot(spots, mCurrentLocation);
        if (spotId != Constants.NOT_IN_PARKINGSPOT &&
                activityTransition != DetectedActivity.UNKNOWN) {
            Spot spot = getSpotFromId(spots, spotId);
            SpotProperties spotProperties = spot.getProperties();
            if (!spotProperties.getState().equals(StatesEnumerations.OCCUPIED.getEstado())) {
                if(!dialogSendAllready){
                    confirmationOfActionDialog(spotId, true);
                }
            } else {
                if(!dialogSendAllready){
                    confirmationOfActionDialog(spotId, false);
                }
            }
        }
    }

    private Spot getSpotFromId(List<Spot> spots, int spotId) {
        Spot result = new Spot();
        for (Spot spot : spots) {
            if (spot.getProperties().getIdFromUrl() == spotId) {
                result = spot;
            }
        }
        return result;
    }

    /**
     * Show a dialog tha could be:
     * OCCUPYING a spot OR FREEING ONE
     * **/
    @SuppressWarnings("MissingPermission")
    private void confirmationOfActionDialog(final int spotIdIn, final boolean isParking) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (isParking) {
            builder.setMessage(R.string.are_you_parking)
                    .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Utils.setNewStateOnSpot(HomeActivity.this, isParking, spotIdIn);
                            userNotResponse = false;
                            geofencingClient.addGeofences(getGeofenceRequest(
                                    getSpotFromId(spots, spotIdIn)), getGeofencePendingIntent());
                        }
                    });
        }else{
            builder.setMessage(R.string.are_you_vacating_a_place)
                .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.setNewStateOnSpot(HomeActivity.this, isParking, spotIdIn);
                        userNotResponse = false;
//                        List<String> geofencesToRemove = new ArrayList<>();
//                        geofencesToRemove.add("ParkinSpot" + spotIn.getId());
//                        geofencingClient.removeGeofences(geofencesToRemove);
                    }
                });
        }
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                userNotResponse = false;
                builder.create().dismiss();
                // User cancelled the dialog
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        dialogSendAllready = true;

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                alertDialog.dismiss();
                if(userNotResponse){
                    Utils.setNewStateOnSpot(HomeActivity.this, isParking, spotIdIn);
                }
                userNotResponse = true;
                dialogSendAllready = false;
                timer.cancel();
            }
        }, Constants.getSecondsInMilliseconds() * 20);
    }

    public boolean isPointInsidePolygon(Spot spot, Location location){
        return PolyUtil.containsLocation(location.getLatitude(),location.getLongitude(),spot.toLatLngList(),
                true);
    }

    public int isPointInsideParkingSpot(List<Spot> ParkingSpot, Location location){
        for(Spot spot : ParkingSpot){
            if (isPointInsidePolygon(spot, location)){
                return spot.getProperties().getIdFromUrl();
            }
        }
        return Constants.NOT_IN_PARKINGSPOT;
    }
    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest(long interval, long fastestInterval) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    /**
     * Registers for activity recognition updates using
     * {@link ActivityRecognitionClient#requestActivityUpdates(long, PendingIntent)}.
     * Registers success and failure callbacks.
     */
    public void requestActivityUpdates() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent());

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                setUpdatesRequestedState(true);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, getString(R.string.activity_updates_not_enabled));
                setUpdatesRequestedState(false);
            }
        });
    }
    /**
     * Removes activity recognition updates using
     * {@link ActivityRecognitionClient#removeActivityUpdates(PendingIntent)}. Registers success and
     * failure callbacks.
     */
    public void removeActivityUpdates() {
        @SuppressLint("MissingPermission")
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                setUpdatesRequestedState(false);
                // Reset the display.
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Failed to enable activity recognition.");
                Toast.makeText(HomeActivity.this,
                        getString(R.string.activity_updates_not_removed),
                        Toast.LENGTH_SHORT).show();
                setUpdatesRequestedState(true);
            }
        });
    }
    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requesting) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.KEY_ACTIVITY_UPDATES_REQUESTED, requesting)
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                Constants.CLIENTE_DATA, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_changepass) {
            Intent changePassIntent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
            startActivity(changePassIntent);
            return true;
        }else if(id == R.id.menu_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.are_you_sure_logout)
                    .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            editor.putString(Constants.USER_TOKEN, Constants.CLIENT_NOT_LOGIN).apply();
                            editor.commit();
                            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logoutIntent);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
