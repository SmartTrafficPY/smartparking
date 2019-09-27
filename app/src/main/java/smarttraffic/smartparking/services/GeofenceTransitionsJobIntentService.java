package smarttraffic.smartparking.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;


public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String LOG_TAG = "GeofenceJobService";

    private static final String CHANNEL_ID = "channel_01";

    public static final String TRANSITION = "TRANSITION";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }
    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        // Test that the reported transition was of interest.
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                startLocationService(triggeringGeofences);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                stopLocationService();
                break;
            default:
                break;
        }
        // Get the geofences that were triggered. A single event can trigger multiple geofences.
        // Get the transition details as a String.
        String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                triggeringGeofences);
        // Send notification and log the transition details.
        broadcastGeofenceTransition(triggeringGeofences, geofenceTransition);
        sendNotification(geofenceTransitionDetails, triggeringGeofences);
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(int geofenceTransition,
                                                List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails, List<Geofence> geofenceList) {
        ArrayList<String> fencesTriggersIdList = new ArrayList<>();
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel();

        for(Geofence geofence : geofenceList){
            fencesTriggersIdList.add(geofence.getRequestId());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_smart_parking)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.notifications_smart_parking))
                .setTimeoutAfter(Constants.getMinutesInMilliseconds() * 5)
                .setColor(Color.GREEN)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void broadcastGeofenceTransition(List<Geofence> triggeringGeofences,
                                             int geofenceTransition) {
        Intent intent = new Intent(Constants.getBroadcastGeofenceTriggerIntent());
        intent.putStringArrayListExtra(Constants.GEOFENCE_TRIGGED,
                namesOfGeofencesTrigger(triggeringGeofences));
        intent.putExtra(TRANSITION, geofenceTransition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void startLocationService(List<Geofence> triggeringGeofences) {
        Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
        serviceIntent.putStringArrayListExtra(Constants.GEOFENCE_TRIGGED,
                namesOfGeofencesTrigger(triggeringGeofences));
        startService(serviceIntent);
        Log.i(LOG_TAG, "Requesting location updates");
    }

    public void stopLocationService() {
        Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
        stopService(serviceIntent);
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
