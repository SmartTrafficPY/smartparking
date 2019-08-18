package smarttraffic.smartparking;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking
 */

public class MyLocationListener implements LocationListener {

    private final Context context;
    private Location location = new Location(LocationManager.GPS_PROVIDER);

    public MyLocationListener(Context context) {
        this.context = context;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        broadcastLocation(location);
        setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void broadcastLocation(Location location) {
        Intent intent = new Intent(Constants.getBroadcastLocationIntent());
        intent.putExtra("latitud", location.getLatitude());
        intent.putExtra("longitud", location.getLongitude());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
