package smarttraffic.smartparking.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.GeofenceErrorMessages;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.activities.HomeActivity;


public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String LOG_TAG = "GeofenceJobService";

    private static final String CHANNEL_ID = "channel_01";

    public static final String TRANSITION = "TRANSITION";
    public static final String GEOFENCE_TRIGGED = "GEOFENCE_TRIGGED";


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
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(LOG_TAG, errorMessage);
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);
            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails, triggeringGeofences, geofenceTransition);
            Log.i(LOG_TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(LOG_TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
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
    private void sendNotification(String notificationDetails, List<Geofence> geofenceList, int transition) {
        ArrayList<String> fencesTriggersIdList = new ArrayList<>();
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
        for(Geofence geofence : geofenceList){
            fencesTriggersIdList.add(geofence.getRequestId());
        }
        notificationIntent.putStringArrayListExtra(GEOFENCE_TRIGGED,
                fencesTriggersIdList);
        notificationIntent.putExtra(TRANSITION, transition);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_smartparking)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.smart_parking))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        builder.setSmallIcon(R.drawable.notification_smartparking)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.smart_parking))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text));

        if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.smart_parking, "Ir a la aplicación",
                    pendingIntent);
        }
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
