package smarttraffic.smartparking.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;


import com.google.android.gms.location.Geofence;

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

public class GeofenceLocationService extends Service implements LocationListener {

    ArrayList<String> listOfAllLotsName = new ArrayList<String>();

    ArrayList<Lot> listOfAllLots = new ArrayList<Lot>();

    public static final String TRANSITION = "TRANSITION";

    private static final String PACKAGE_NAME = "smarttraffic.smartparking.geofenceService";

    private static final String CHANNEL_ID = "geofence_channel";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".geofence";

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long UPDATE_INTERVAL =
            Constants.getMinutesInMilliseconds() * 5;
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 13;

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private NotificationManager mNotificationManager;

    private Handler mServiceHandler;
    /**
     * The current location.
     */
    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);

    private LocationManager locationManager;

    public GeofenceLocationService() {
        // Persistence Constructor
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(LocationUpdatesService.class.getSimpleName());
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
        listOfAllLotsName = intent.getStringArrayListExtra(Constants.LOTS_NAME_LIST);
        getAllLotsFromSharedPref();
        startForeground(NOTIFICATION_ID, getNotification());
        requestLocationUpdates();
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                Constants.SETTINGS, MODE_PRIVATE);
        Utils.setRequestingLocationUpdates(this, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    sharedPreferences.getLong(Constants.LOCATION_TIME_UPDATE_SETTINGS, UPDATE_INTERVAL)
                    , 0, this);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager != null){
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        int transition = 0;
        ArrayList<String> listOfLotsTriggerd = new ArrayList<String>();
        if(listOfAllLots != null && !listOfAllLots.isEmpty()){
            for(Lot lot : listOfAllLots){
                if(location.distanceTo(lot.getProperties().getLocationCenter()) <= lot.getProperties().getRadio()){
                    listOfLotsTriggerd.add(lot.getProperties().getName());
                    if(haveBeenLongTimeInside() &&
                            Utils.getLastGeofenceTrigger(GeofenceLocationService.this).equals(lot.getProperties().getName())){
                        transition = Geofence.GEOFENCE_TRANSITION_DWELL;
                        sendNotification(GeofenceLocationService.this, "Mucho tiempo de permanencia en " + lot.getProperties().getName());
                        stopLocationService(GeofenceLocationService.this);
                    }else{
                        transition = Geofence.GEOFENCE_TRANSITION_ENTER;
                        Utils.setLastGeofenceTrigger(GeofenceLocationService.this, lot.getProperties().getName());
                        Utils.setLastTimeInsideGeofence(GeofenceLocationService.this, false);
                        sendNotification(GeofenceLocationService.this, "Entrada al predio " + lot.getProperties().getName());
                        startLocationService(GeofenceLocationService.this);
                    }
                }else{
                    Utils.setLastGeofenceTrigger(GeofenceLocationService.this,"");
                    Utils.setLastTimeInsideGeofence(GeofenceLocationService.this, true);
                    stopLocationService(GeofenceLocationService.this);
                    transition = Geofence.GEOFENCE_TRANSITION_DWELL;
                }
            }
        }
        broadcastGeofenceTransition(GeofenceLocationService.this, listOfLotsTriggerd,transition);
    }

    private boolean haveBeenLongTimeInside() {
        Long today = System.currentTimeMillis();
        if((today - Utils.getLastTimeInsideGeofence(GeofenceLocationService.this)) / Constants.getMinutesInMilliseconds() >= 20){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //No need to implement...
    }

    @Override
    public void onProviderEnabled(String provider) {
        //No need to implement...

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void sendNotification(Context context, String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
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
            builder.setChannelId(CHANNEL_ID); // Channel ID
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
}
