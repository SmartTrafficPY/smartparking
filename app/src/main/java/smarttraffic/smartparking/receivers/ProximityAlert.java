package smarttraffic.smartparking.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.activities.HomeActivity;

import static smarttraffic.smartparking.R.mipmap.smartparking_logo_round;

/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */

public class ProximityAlert extends BroadcastReceiver{

    private static final String LOG_TAG = "ProximityAlert";
    public static final String EVENT_ID_INTENT_EXTRA = "EventIDIntentExtraKey";


    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         *
         * Here should send the notification...
         *
         * **/

        sendNotification(context,1, "SmartParking",
                "This is just a test to see the Notification");

        long eventID = intent.getLongExtra(EVENT_ID_INTENT_EXTRA, -1);
        Log.v(LOG_TAG,"Proximity Alert Intent Received, eventID = "+ eventID);
    }

    private Notification setNotification(Context context, String textTitle, String textContent) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //Create the builder for the notifications...
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.getChannelId())
                .setSmallIcon(smartparking_logo_round)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        return builder.build();
    }

    private void setNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "SmartParking Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.getChannelId(), channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void sendNotification(Context context, int idNotification, String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Constants.getNotificationService());
        setNotificationChannel(notificationManager);
        Notification notification = setNotification(context, title, message);
        notificationManager.notify(idNotification, notification);
    }

}
