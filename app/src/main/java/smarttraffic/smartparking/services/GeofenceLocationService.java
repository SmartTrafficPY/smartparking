package smarttraffic.smartparking.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import retrofit2.http.GET;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.dataModels.Lots.Lot;



/**
 * Created by Joaquin on 11/2019.
 * <p>
 * smarttraffic.smartparking.services
 */

public class GeofenceLocationService extends Service{

    ArrayList<String> listOfAllLotsName = new ArrayList<String>();

    ArrayList<Lot> listOfAllLots = new ArrayList<Lot>();

    public static final String TRANSITION = "TRANSITION";


    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.geofenceservice";

    private static final String TAG = GeofenceLocationService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "geofence_channel";

    private static final String TRANSITION_CHANNEL = "Transicion SmartParking";

    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL = Constants.getMinutesInMilliseconds() * 6;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL =
            Constants.getMinutesInMilliseconds() * 5;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 66118;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    public GeofenceLocationService() {
    }

    @Override
    public void onCreate() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
                checkForTransitionsIntoLots(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
//        startForeground(NOTIFICATION_ID, getNotification());
        getAllLotsFromSharedPref();
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates(ArrayList<String> lotsNameList) {
        Log.i(TAG, "Requesting location updates");
        listOfAllLotsName = lotsNameList;
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), GeofenceLocationService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(this.getString(R.string.geofence_background))
                .setContentTitle(Utils.getGeofenceTitle(this))
                .setOngoing(true)
                .setColor(Color.GREEN)
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.drawable.notifications_smart_parking)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.notifications_smart_parking))
                .setTicker("SmartParking geofence")
                .setWhen(System.currentTimeMillis());
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;
//
//        // Notify anyone listening for broadcasts about the new location.
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public GeofenceLocationService getService() {
            return GeofenceLocationService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    private void broadcastGeofenceTransition(Context context, ArrayList<String> triggeringGeofences,
                                             int geofenceTransition) {
        Intent intent = new Intent(Constants.getBroadcastGeofenceTriggerIntent());
        intent.putStringArrayListExtra(Constants.GEOFENCE_TRIGGED,
                triggeringGeofences);
        intent.putExtra(TRANSITION, geofenceTransition);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void getAllLotsFromSharedPref(){
        if(listOfAllLotsName != null && !listOfAllLotsName.isEmpty()){
            for(String nameLot : listOfAllLotsName){
                listOfAllLots.add(Utils.getLotsSaved(GeofenceLocationService.this, nameLot));
            }
        }
    }

    public void startLocationService(Context context) {
        Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
        context.startService(serviceIntent);
    }

    public void stopLocationService(Context context) {
        Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
        context.stopService(serviceIntent);
    }

    private boolean haveBeenLongTimeInside() {
        Long lastUpdate = Utils.getLastTimeInsideGeofence(GeofenceLocationService.this);
        Long today = System.currentTimeMillis();
        long minutesPast = ((today - lastUpdate) / Constants.getMinutesInMilliseconds());
        if(!lastUpdate.equals(0)){
            if(minutesPast >= 20){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public void checkForTransitionsIntoLots(Location location) {
        mLocation = location;
        int transition = Utils.getLastTransitionGeofence(GeofenceLocationService.this);
        ArrayList<String> listOfLotsTriggerd = new ArrayList<String>();
        if(listOfAllLots != null && !listOfAllLots.isEmpty()){
            for(Lot lot : listOfAllLots){
                if(location.distanceTo(lot.getProperties().getLocationCenter()) <= lot.getProperties().getRadio()){
                    listOfLotsTriggerd.add(lot.getProperties().getName());
                    if(haveBeenLongTimeInside() &&
                            Utils.getLastGeofenceTrigger(GeofenceLocationService.this).equals(lot.getProperties().getName())){
                        transition = Geofence.GEOFENCE_TRANSITION_DWELL;
                        stopLocationService(GeofenceLocationService.this);
                        Utils.setLastTransitionGeofenceNotify(GeofenceLocationService.this, transition);
                    }else{
                        transition = Geofence.GEOFENCE_TRANSITION_ENTER;
                        if(Utils.getLastTransitionGeofence(GeofenceLocationService.this) != transition){
                            sendNotification(GeofenceLocationService.this, "Entrada al predio " + lot.getProperties().getName());
                            startLocationService(GeofenceLocationService.this);
                            Utils.setLastGeofenceTrigger(GeofenceLocationService.this, lot.getProperties().getName());
                            Utils.setLastTimeInsideGeofence(GeofenceLocationService.this, false);
                            Utils.setLastTransitionGeofenceNotify(GeofenceLocationService.this, transition);
                        }
                    }
                }else{
                    if(Utils.getLastTransitionGeofence(GeofenceLocationService.this) != Geofence.GEOFENCE_TRANSITION_EXIT){
                        transition = Geofence.GEOFENCE_TRANSITION_EXIT;
                        Utils.setLastTransitionGeofenceNotify(GeofenceLocationService.this, transition);
                    }
                    Utils.setLastGeofenceTrigger(GeofenceLocationService.this,"");
                    Utils.setLastTimeInsideGeofence(GeofenceLocationService.this, true);
                    stopLocationService(GeofenceLocationService.this);
                }
            }
        }
        broadcastGeofenceTransition(GeofenceLocationService.this, listOfLotsTriggerd,transition);
    }

    private void sendNotification(Context context, String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TRANSITION_CHANNEL)
                .setSmallIcon(R.drawable.notifications_smart_parking)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.notifications_smart_parking))
                .setTimeoutAfter(Constants.getMinutesInMilliseconds() * 5)
                .setColor(Color.GREEN)
                .setContentTitle(notificationDetails)
                .setContentText(context.getString(R.string.geofence_transition_notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(TRANSITION_CHANNEL); // Channel ID
        }
        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
