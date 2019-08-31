package smarttraffic.smartparking.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.StatesEnumerations;
import smarttraffic.smartparking.dataModels.SmartParkingLot;
import smarttraffic.smartparking.dataModels.SmartParkingSpot;
import smarttraffic.smartparking.fragments.AboutFragment;
import smarttraffic.smartparking.fragments.ChangePassFragment;
import smarttraffic.smartparking.fragments.HomeFragment;
import smarttraffic.smartparking.fragments.LogOutFragment;
import smarttraffic.smartparking.fragments.SettingsFragment;
import smarttraffic.smartparking.receivers.GeofenceBroadcastReceiver;
import smarttraffic.smartparking.services.DetectedActivitiesService;
import smarttraffic.smartparking.services.GeofenceTransitionsJobIntentService;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class HomeActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.navview)
    NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;

    private FusedLocationProviderClient mFusedLocationClient;
    private ActivityRecognitionClient mActivityRecognitionClient;

    private SettingsClient mSettingsClient;
    int transitionType;
    int confidence;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private ArrayList<SmartParkingSpot> spots = new ArrayList<>();
    private ArrayList<String> geofencesTrigger = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private GeofencingClient geofencingClient;
    private PendingIntent mGeofencePendingIntent;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);
        createLocationRequest(Constants.getSecondsInMilliseconds() * 2,
                Constants.getSecondsInMilliseconds());
        buildLocationSettingsRequest();

        mSettingsClient = LocationServices.getSettingsClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);

        if(getIntent().getExtras() == null){
            addParkingLotsGeofences();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_TRANSITION_ACTIVITY_INTENT)) {
                    transitionType = intent.getIntExtra(Constants.ACTIVITY_TYPE_TRANSITION, -1);
                    confidence = intent.getIntExtra(Constants.ACTIVITY_CONFIDENCE_TRANSITION, -1);
                    Log.i(LOG_TAG, "Transition type: " + transitionType + "confidence " + confidence + "%");
                }
            }
        };

        createLocationCallback();
        getTransitionsFromGeofences();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment initialFragment = null;
        try {
            initialFragment = (Fragment) HomeFragment.class.newInstance();
            Bundle data = new Bundle();
            data.putSerializable("SmartParkingSpotList",  (Serializable) spots);
            data.putStringArrayList(Constants.GEOFENCE_TRIGGER_ID, geofencesTrigger);
            initialFragment.setArguments(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, initialFragment).commit();
        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
    }

    private void getTransitionsFromGeofences() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = getIntent();
        geofencesTrigger = intent.getStringArrayListExtra(
                GeofenceTransitionsJobIntentService.GEOFENCE_TRIGGED);
        final Handler handler = new Handler();
        final long delay = Constants.getMinutesInMilliseconds();
        Runnable cronJob = new Runnable(){
            public void run(){
                getSpotsFromGeofence(geofencesTrigger);
                handler.postDelayed(this, delay);
            }
        };
        switch(intent.getIntExtra(GeofenceTransitionsJobIntentService.TRANSITION,
                -1)){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.i(LOG_TAG, "Enter Transition");
                mNotificationManager.cancel(0);
                if (checkPermissions()) {
                    startLocationUpdates(mLocationRequest);
                } else if (!checkPermissions()) {
                    requestPermissions();
                }
                handler.postDelayed(cronJob, delay);
                requestActivityUpdates();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.i(LOG_TAG, "Exit Transition");
                stopLocationUpdates();
                removeActivityUpdates();
                handler.removeCallbacks(cronJob);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.i(LOG_TAG, "Dwell Transition");
                break;
            default:
                Log.i(LOG_TAG, "No transition detected");
        }
    }

    private void getSpotsFromGeofence(ArrayList<String> geofencesTrigger) {
        for(String geofenceTrigger : geofencesTrigger){
            getAllSpotFrom(geofenceTrigger);
        }
    }

    private void getAllSpotFrom(String geofencesTrigger) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL_HOME2)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<List<SmartParkingSpot>> call = smartParkingAPI.getAllSpotsInLot(geofencesTrigger);

        call.enqueue(new Callback<List<SmartParkingSpot>>() {
            @Override
            public void onResponse(Call<List<SmartParkingSpot>> call, Response<List<SmartParkingSpot>> response) {
                switch (response.code()) {
                    case 200:
                        spots = (ArrayList<SmartParkingSpot>) response.body();
                        break;
                    default:
                        Toast.makeText(HomeActivity.this, "Por alguna razón no fue posible la conexión",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<SmartParkingSpot>> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG,t.toString());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
        getTransitionsFromGeofences();
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
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.getBroadcastLocationIntent()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        stopLocationUpdates();
        removeActivityUpdates();
    }

    private void addParkingLotsGeofences() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL_HOME2)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<List<SmartParkingLot>> call = smartParkingAPI.getAllLots();

        call.enqueue(new Callback<List<SmartParkingLot>>() {
            @Override
            public void onResponse(Call<List<SmartParkingLot>> call, Response<List<SmartParkingLot>> response) {
                switch (response.code()) {
                    case 200:
                        List<SmartParkingLot> lots = response.body();
                        ArrayList<Geofence> geofenceList = new ArrayList<>();
                        for(SmartParkingLot lot : lots){
                            geofenceList.add(generateGeofence(lot.getLatitud_center(),
                                    lot.getLongitud_center(),
                                    lot.getRadio(),
                                    lot.getName()));
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
            public void onFailure(Call<List<SmartParkingLot>> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG,t.toString());
            }
        });
    }
    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private Geofence generateGeofence(double latitude, double longitud, float radius, String nameId) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(nameId)
                .setCircularRegion(
                        latitude,
                        longitud,
                        radius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
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
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(LOG_TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
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
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.home_menu:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.menu_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.menu_changepass:
                fragmentClass = ChangePassFragment.class;
                break;
            case R.id.menu_logout:
                fragmentClass = LogOutFragment.class;
                break;
            case R.id.menu_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            Bundle data = new Bundle();
            data.putStringArrayList(Constants.GEOFENCE_TRIGGER_ID, geofencesTrigger);
            fragment.setArguments(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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
    /**
      - Is User in a spot?
      - Is the user STILL in that spot?
     * THEN, THE USER COULD BE:
     * PARKING:
     *      - Is the spot free?
     *      THEN: show dialog for secure the action...
     *FREEING A SPOT:
     *      - Is the spot occupied by the SAME user?
     *      THEN: show dialog for secure the action...**/
    private void checkForUserLocation(Location mCurrentLocation) {
        int spotId = isPointInsideParkingSpot(spots, mCurrentLocation);
        if(spotId != Constants.NOT_IN_PARKINGSPOT && transitionType == DetectedActivity.STILL){
            SmartParkingSpot spot = getSpotFromId(spots, spotId);
            if(spot.getStatus() == StatesEnumerations.FREE.getEstado() ||
                    spot.getStatus() == StatesEnumerations.UNKNOWN.getEstado()){
                confirmationOfActionDialog(true);
            }else{
//                if(spot.getUserChanged() == actualUser){
//                    confirmationOfActionDialog(false);
//                }
            }
        }
    }

    private SmartParkingSpot getSpotFromId(ArrayList<SmartParkingSpot> spots, int spotId){
        SmartParkingSpot result = new SmartParkingSpot();
        for(SmartParkingSpot spot : spots){
            if(spot.getId() == spotId){
                result = spot;
            }
//            if(userId == spot.getUserChanged()){
//                break;
//            }
        }
        return result;
    }
    /**
     * Show a dialog tha could be:
     * OCCUPYING a spot OR FREEING ONE
     * **/
    private void confirmationOfActionDialog(boolean isParking){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        if(isParking){
            builder.setMessage(R.string.are_you_parking)
                    .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // call REST service to UPDATE STATE to OCCUPIED by userId
                            // addGeofence(HERE)...
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
        }else{
            builder.setMessage(R.string.are_you_vacating_a_place)
                .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // call REST service to UPDATE STATE to FREE
                        // removeGeofence(HERE)...
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        }
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                alertDialog.dismiss();
                //if he was to park o to make vacating a spot, make it...
                timer.cancel();
            }
        }, 10000);
    }

    public boolean isPointInsidePolygon(SmartParkingSpot spot, Location location){
        return PolyUtil.containsLocation(location.getLatitude(),location.getLongitude(),spot.toLatLngList(),
                true);
    }

    public int isPointInsideParkingSpot(ArrayList<SmartParkingSpot> ParkingSpot, Location location){
        for(SmartParkingSpot spot : ParkingSpot){
            if (isPointInsidePolygon(spot, location)){
                return spot.getId();
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
    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.KEY_ACTIVITY_UPDATES_REQUESTED, false);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constants.KEY_DETECTED_ACTIVITIES)) {
            //activity detected needed...
        }
    }
}
