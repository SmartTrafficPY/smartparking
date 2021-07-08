package smarttraffic.smartparking.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.activities.HomeActivity;

import static smarttraffic.smartparking.Utils.getGeofencesTrigger;

public class LocationUpdatesService extends Service implements LocationListener{

    private static final String PACKAGE_NAME = "smarttraffic.smartparking.services";

    private static final String CHANNEL_ID = "location_updates_channel";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long UPDATE_INTERVAL =
            Constants.getSecondsInMilliseconds() * 5;
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 5;

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

    List<List<LatLng>> lotsPolygons = new ArrayList<>();

    public LocationUpdatesService() {
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
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);
        ArrayList<String> namesOfGeofencesTrigger =
                getGeofencesTrigger(this);
        if(!Utils.returnListOfGateways(this, namesOfGeofencesTrigger).isEmpty()){
            lotsPolygons = Utils.returnListOfGateways(this, namesOfGeofencesTrigger);
        }
        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        } else {
            startForeground(NOTIFICATION_ID, getNotification());
            requestLocationUpdates();
        }
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
            if(sharedPreferences.getBoolean(Constants.LOCATIONS_REQUEST_SETTINGS_CHANGE, false)){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.LOCATIONS_REQUEST_SETTINGS_CHANGE,false).apply();
                editor.commit();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        sharedPreferences.getLong(Constants.LOCATION_TIME_UPDATE_SETTINGS, UPDATE_INTERVAL)
                        , 0, this);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        sharedPreferences.getLong(Constants.LOCATION_TIME_UPDATE_SETTINGS, UPDATE_INTERVAL)
                        , 0, this);
            }
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

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.notifications_smart_parking, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.notifications_smart_parking, getString(R.string.remove_location_updates),
                        servicePendingIntent)
                .setContentText(this.getString(R.string.foreground_notification))
                .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setColor(Color.GREEN)
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.drawable.notifications_smart_parking)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.notifications_smart_parking))
                .setTicker("SmartParking")
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

    private void broadcastLocation(Location location) {
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        broadcastLocation(mLocation);
        Utils.compareUncomingGateways(LocationUpdatesService.this, mLocation, lotsPolygons);
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

}
