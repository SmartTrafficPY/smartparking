package smarttraffic.smartparking.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.activities.HomeActivity;


/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.receivers
 */

public class RemoveAlarmReceiver extends BroadcastReceiver {

    private GeofencingClient geofencingClient;
    private PendingIntent mGeofencePendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(getGeofencePendingIntent(context));
        Utils.geofencesSetUp(context,false);
    }

    private PendingIntent getGeofencePendingIntent(Context context) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);

        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
}
