package smarttraffic.smartparking.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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
import org.osmdroid.views.overlay.Marker;
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
import smarttraffic.smartparking.dataModels.Spots.Spot;
import smarttraffic.smartparking.dataModels.Spots.SpotList;
import smarttraffic.smartparking.dataModels.Spots.SpotProperties;
import smarttraffic.smartparking.receivers.AddAlarmReceiver;
import smarttraffic.smartparking.receivers.GeofenceBroadcastReceiver;
import smarttraffic.smartparking.receivers.RemoveAlarmReceiver;
import smarttraffic.smartparking.services.DetectedActivitiesService;
import smarttraffic.smartparking.services.GeofenceTransitionsJobIntentService;
import smarttraffic.smartparking.services.LocationUpdatesService;

import static smarttraffic.smartparking.Interceptors.ReceivedTimeStampInterceptor.X_TIMESTAMP;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.mapFragment)
    MapView mapView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.buttonRecenter)
    ImageButton buttonRecenter;
    @BindView(R.id.buttonAbout)
    ImageButton buttonAbout;

    AddAlarmReceiver addAlarmReceiver = new AddAlarmReceiver();
    RemoveAlarmReceiver removeAlarmReceiver = new RemoveAlarmReceiver();
    IntentFilter filter = new IntentFilter();

    private ActivityRecognitionClient mActivityRecognitionClient;

    int activityTransition;
    int geofenceTransition;
    int confidence;
    boolean userNotResponse = true;
    boolean dialogSendAllready = false;
    Polygon polygon = new Polygon();
    private Location mCurrentLocation;
    private List<Spot> spots = new ArrayList<Spot>();
    private ArrayList<String> geofencesTrigger = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver geofenceReceiver;
    private BroadcastReceiver locationReceiver;
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

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);

        registerReceiver(addAlarmReceiver, filter);
        registerReceiver(removeAlarmReceiver, filter);
        Utils.addAlarmsGeofencingTask(HomeActivity.this);

        setMapView();

        buttonRecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationOverlay != null){
                    mapView.getController().setCenter(mLocationOverlay.getMyLocation());
                }
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog settingsDialog = new Dialog(HomeActivity.this);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.about_layout
                        , null));
                settingsDialog.show();
            }
        });

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
                            Constants.GEOFENCE_TRIGGED);
                    geofenceTransition = intent.getIntExtra(
                            GeofenceTransitionsJobIntentService.TRANSITION,
                            -1);
                    managerOfTransitions();
                }
            }
        };

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCurrentLocation = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
                if (mCurrentLocation != null) {
                    checkForUserLocation(mCurrentLocation);
                }
            }
        };
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
        //add all overlays
        mapView.setBuiltInZoomControls(false);
        addOverlays();
    }

    private void setMarkersOnMap(List<GeoPoint> geoPoints, String state) {
        Drawable marker = getDrawable(R.drawable.unknown_marker);
        Marker startMarker = new Marker(mapView);
        String stateValue = "Desconocido";
        startMarker.setPosition(Utils.getCentroid(geoPoints));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (state.equals(StatesEnumerations.FREE.getEstado())) {
            marker = getDrawable(R.drawable.free_marker);
            stateValue = "Libre";
        } else if (state.equals(StatesEnumerations.OCCUPIED.getEstado())) {
            marker = getDrawable(R.drawable.occupied_marker);
            stateValue = "Ocupado";
        }
        startMarker.setTitle(stateValue);
        startMarker.setTextLabelBackgroundColor(R.color.white);
        startMarker.setTextLabelForegroundColor(R.color.white);
        startMarker.setIcon(marker);
        mapView.getOverlays().add(startMarker);
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
        //TODO: set better movement icon...
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
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                Constants.SETTINGS, MODE_PRIVATE);
        getSpotsFromGeofence(geofencesTrigger, false);
        final Handler handler = new Handler();
        final long delay = sharedPreferences.getLong(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS,
                Constants.getSecondsInMilliseconds() * 45);
        Runnable cronJob = new Runnable() {
            public void run() {
                getSpotsFromGeofence(geofencesTrigger, true);
                handler.postDelayed(this, delay);
            }
        };
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                handler.postDelayed(cronJob, delay);
                requestActivityUpdates();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                removeActivityUpdates();
                handler.removeCallbacks(cronJob);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                break;
            default:
        }
    }

    private void getSpotsFromGeofence(ArrayList<String> geofencesTrigger, boolean isForUpdate) {
        if(geofencesTrigger != null) {
            if(isForUpdate){
                updatesSpotsFromGeofence();
            }else{
                for (String geofenceTrigger : geofencesTrigger) {
                    getSpotsGeographicValues(geofenceTrigger);
                }
            }
        }
    }

    private void getSpotsGeographicValues(String geofencesTrigger) {
        final SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.SETTINGS,
                MODE_PRIVATE);
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
                        if(!testSpots.isEmpty()){
                            spots = testSpots.getFeatures();
                        }
                        if(spots != null){
                            mapView.getOverlays().clear();
                            for (Spot spot : spots) {
                                if(sharedPreferences.getString(Constants.DRAW_SETTINGS,
                                        Constants.POLYGON_TO_DRAW_SETTINGS).equals(Constants.POLYGON_TO_DRAW_SETTINGS)){
                                    drawPolygon(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    Utils.polygonWereDraw(HomeActivity.this,true);
                                }else{
                                    setMarkersOnMap(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    Utils.polygonWereDraw(HomeActivity.this,false);
                                }
                            }
                            addOverlays();
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<SpotList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updatesSpotsFromGeofence() {
        final SharedPreferences sharedPreferencesSettings = this.getSharedPreferences(Constants.SETTINGS,
                MODE_PRIVATE);
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
        Call<HashMap<String, String>> call = smartParkingAPI.getNearbySpots("application/vnd.geo+json", nearbyLocation);

        call.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                switch (response.code()) {
                    case 200:
                        HashMap<String, String> changedSpots = response.body();
                        List<Spot> spotsUpdated = Utils.updateSpots(changedSpots, spots);
                        if(spotsUpdated != null){
                            if(Utils.firstDrawShape(HomeActivity.this) == Constants.POLYGON_TO_DRAW_SETTINGS){
                                if(sharedPreferencesSettings.getString(Constants.DRAW_SETTINGS,
                                        Constants.POLYGON_TO_DRAW_SETTINGS).equals(Constants.POLYGON_TO_DRAW_SETTINGS)){
                                    for(Spot spot : spotsUpdated){
                                        drawPolygon(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    }
                                }else{
                                    mapView.getOverlayManager().remove(polygon);
                                    for(Spot spot : spots){
                                        setMarkersOnMap(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    }
                                }
                            }else{
                                if(sharedPreferencesSettings.getString(Constants.DRAW_SETTINGS,
                                        Constants.POLYGON_TO_DRAW_SETTINGS).equals(Constants.POLYGON_TO_DRAW_SETTINGS)){
                                    mapView.getOverlays().clear();
                                    for(Spot spot : spots){
                                        drawPolygon(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    }
                                    addOverlays();
                                }else{
                                    for(Spot spot : spotsUpdated){
                                        setMarkersOnMap(Utils.spotToListOfGeoPoints(spot), spot.getProperties().getState());
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utils.isDayOfWeek() && Utils.getGeofenceStatus(this)){
            addParkingLotsGeofences();
        }
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeActivityUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_TRANSITION_ACTIVITY_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceReceiver,
                new IntentFilter(Constants.getBroadcastGeofenceTriggerIntent()));
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        registerReceiver(addAlarmReceiver, filter);
        if(Utils.getGeofenceStatus(HomeActivity.this)){
            if(!Utils.isDayOfWeek()){
                removeGeofences();
            }
        }else if(Utils.isDayOfWeek()){
            addParkingLotsGeofences();
        }
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        unregisterReceiver(addAlarmReceiver);
        unregisterReceiver(removeAlarmReceiver);
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
                        addGeofences(geofenceList);
                        Utils.geofencesSetUp(HomeActivity.this,true);
                        //TODO: uncomment to work with the gateway...
//                        Utils.saveListOfGateways(HomeActivity.this, response.body());
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
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.home_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.button_accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    Constants.getRequestPermissionsRequestCode());
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.getRequestPermissionsRequestCode());
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.getRequestPermissionsRequestCode()) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.home_layout),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
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
                        })
                        .show();
            }
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
        Utils.geofencesSetUp(HomeActivity.this,true);
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    public void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        geofencingClient.removeGeofences(getGeofencePendingIntent());
        Utils.geofencesSetUp(HomeActivity.this,false);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

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
                50, "Tu vehiculo en " + spot.getProperties().getIdFromUrl(), true));
        return builder.build();
    }

    private void checkForUserLocation(Location mCurrentLocation) {
        int spotId = isPointInsideParkingSpot(spots, mCurrentLocation);
        //TODO: add more activityTransition detail...
        if (spotId != Constants.NOT_IN_PARKINGSPOT &&
                (activityTransition != DetectedActivity.UNKNOWN)) {
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
                            Intent serviceIntent = new Intent(HomeActivity.this,
                                    LocationUpdatesService.class);
                            stopService(serviceIntent);
                        }
                    });
        }else{
            builder.setMessage(R.string.are_you_vacating_a_place)
                .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.setNewStateOnSpot(HomeActivity.this, isParking, spotIdIn);
                        userNotResponse = false;
//                        List<String> geofencesToRemove = new ArrayList<>();
//                        geofencesToRemove.add("Tu vehiculo en " + spotIn.getId());
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

        final Timer dialogtimer = new Timer();
        dialogtimer.schedule(new TimerTask() {
            public void run() {
                alertDialog.dismiss();
                if(userNotResponse){
                    Utils.setNewStateOnSpot(HomeActivity.this, isParking, spotIdIn);
                }
                userNotResponse = true;
            }
        }, Constants.getSecondsInMilliseconds() * 35);
        dialogtimer.schedule(new TimerTask() {
            public void run() {
                dialogSendAllready = false;
                dialogtimer.cancel();
            }
        }, Constants.getSecondsInMilliseconds() * 40);
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
                            Intent logoutIntent = new Intent(HomeActivity.this,
                                    BifurcationActivity.class);
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
        }else if(id == R.id.setting_menu){
            Intent settingsActivity = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(settingsActivity);
        }
        return super.onOptionsItemSelected(item);
    }

}
