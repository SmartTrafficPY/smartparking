package smarttraffic.smartparking.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.location.GeofencingEvent;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.services.LocationUpdatesService;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "channel_01";

    public static final String TRANSITION = "TRANSITION";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        // Get the geofences that were triggered. A single event can trigger multiple geofences.
        // Get the transition details as a String.
        String geofenceTransitionDetails = getGeofenceTransitionDetails(context, geofenceTransition,
                triggeringGeofences);
        // Test that the reported transition was of interest.
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                sendNotification(context, geofenceTransitionDetails, triggeringGeofences);
                startLocationService(context, triggeringGeofences);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                stopLocationService(context);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                stopLocationService(context);
                break;
            default:
                break;
        }
        // Send notification and log the transition details.
        broadcastGeofenceTransition(context, triggeringGeofences, geofenceTransition);
    }

    private String getGeofenceTransitionDetails(Context context, int geofenceTransition,
        List<Geofence> triggeringGeofences) {

            String geofenceTransitionString = getTransitionString(context, geofenceTransition);

            // Get the Ids of each geofence that was triggered.
            ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
            for (Geofence geofence : triggeringGeofences) {
                triggeringGeofencesIdsList.add(geofence.getRequestId());
            }
            String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

            return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
        }

    private void sendNotification(Context context, String notificationDetails, List<Geofence> geofenceList) {
            ArrayList<String> fencesTriggersIdList = new ArrayList<>();
            // Get an instance of the Notification manager
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            createNotificationChannel(context);

            for(Geofence geofence : geofenceList){
                fencesTriggersIdList.add(geofence.getRequestId());
            }

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

    private String getTransitionString(Context context, int transitionType) {
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    return context.getString(R.string.geofence_transition_entered);
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    return context.getString(R.string.geofence_transition_exited);
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    return context.getString(R.string.geofence_transition_dwell);
                default:
                    return context.getString(R.string.unknown_geofence_transition);
            }
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

    private void broadcastGeofenceTransition(Context context, List<Geofence> triggeringGeofences,
        int geofenceTransition) {
            Intent intent = new Intent(Constants.getBroadcastGeofenceTriggerIntent());
            intent.putStringArrayListExtra(Constants.GEOFENCE_TRIGGED,
                    namesOfGeofencesTrigger(triggeringGeofences));
            intent.putExtra(TRANSITION, geofenceTransition);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    public void startLocationService(Context context, List<Geofence> triggeringGeofences) {
            Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
            Utils.saveGeofencesTrigger(context,namesOfGeofencesTrigger(triggeringGeofences));
            context.startService(serviceIntent);
        }

    public void stopLocationService(Context context) {
            Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
            context.stopService(serviceIntent);
        }

    public ArrayList<String> namesOfGeofencesTrigger(List<Geofence> triggeringGeofences){
            ArrayList<String> fencesTriggered = new ArrayList<>();
            if(triggeringGeofences != null){
                for(Geofence geofence : triggeringGeofences){
                    fencesTriggered.add(geofence.getRequestId());
                }
            }
            return fencesTriggered;
        }

}
