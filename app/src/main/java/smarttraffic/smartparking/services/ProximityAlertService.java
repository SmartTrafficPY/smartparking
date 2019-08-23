package smarttraffic.smartparking.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import smarttraffic.smartparking.Constants;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking.services
 */

public class ProximityAlertService extends Service implements LocationListener {

    public static final String LATITUDE_INTENT_KEY = "LATITUDE_INTENT_KEY";
    public static final String LONGITUDE_INTENT_KEY = "LONGITUDE_INTENT_KEY";
    public static final String RADIUS_INTENT_KEY = "RADIUS_INTENT_KEY";
    private static final String LOG_TAG = "ProximityAlertService";
    private static final String PROX_ALERT_INTENT = "smarttraffic.smartparking.services.ProximityAlert";

    private double latitude;
    private double longitude;
    private float radius;
    private LocationManager locationManager;
    private boolean inProximity;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Location bestLocation = null;

        latitude = intent.getDoubleExtra(LATITUDE_INTENT_KEY, Double.MIN_VALUE);
        longitude = intent.getDoubleExtra(LONGITUDE_INTENT_KEY,
                Double.MIN_VALUE);
        radius = intent.getFloatExtra(RADIUS_INTENT_KEY, Float.MIN_VALUE);

        Location pointOfInterest = new Location("systemProvider");
        pointOfInterest.setLatitude(latitude);
        pointOfInterest.setLongitude(longitude);

        for (String provider : locationManager.getProviders(false)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location location = locationManager.getLastKnownLocation(provider);

            if (bestLocation == null) {
                bestLocation = location;
            } else {
                // getAccuracy() describes the deviation in meters. So, the
                // smaller the number, the better the accuracy.
                if (location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
        }

        if (bestLocation != null) {
            if (bestLocation.distanceTo(pointOfInterest) <= radius) {
                inProximity = true;
            } else {
                inProximity = false;
            }
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, Constants.getMinutesInMilliseconds() * 10,
                Constants.getMinDistanceChangeForUpdates(), this);

        // meaning that the service should be moved back into the
        // started state (as if onStartCommand() had been called), but do not
        // re-deliver
        // the Intent tov  onStartCommand()
//        startForeground();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        float distance = location.distanceTo(location);
        if (distance <= radius && !inProximity) {
            inProximity = true;
            Log.i(LOG_TAG, "Entering Proximity");
            Toast.makeText(getBaseContext(), "Entering Proximity by service",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(
                    PROX_ALERT_INTENT);
            intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
            sendBroadcast(intent);

        } else if (distance > radius && inProximity) {
            inProximity = false;
            Log.i(LOG_TAG, "Exiting Proximity");
            Toast.makeText(getBaseContext(), "Exiting Proximity",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(
                    PROX_ALERT_INTENT);
            intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
            sendBroadcast(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        Toast.makeText(getBaseContext(), "Stoping the service!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private float getDistance(Location location) {
        float[] results = new float[1];

        Location.distanceBetween(latitude, longitude, location.getLatitude(),
                location.getLongitude(), results);

        return results[0];
    }
}
