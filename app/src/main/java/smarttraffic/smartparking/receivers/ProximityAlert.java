package smarttraffic.smartparking.receivers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.MyLocationListener;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.activities.HomeActivity;

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
        final Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,
                false);
        final int eventId = intent.getIntExtra(EVENT_ID_INTENT_EXTRA, 0);
        String parkingLot;

        switch (eventId){
            case 1:
                parkingLot = "UCAmpus";
                break;
            case 2:
                parkingLot = "Home";
                break;
            case 3:
                parkingLot = "San Rafael Church";
                break;
            default:
                parkingLot = "";
        }

        startUpdatingLocation(context,entering);

        if (entering) {
            sendNotification(context,1, "SmartParking",
                    "You are near " + parkingLot + " check parking spots");
        } else {
            sendNotification(context,1, "SmartParking",
                    "You are leaving " + parkingLot);
        }

        long eventID = intent.getLongExtra(EVENT_ID_INTENT_EXTRA, -1);
        Log.v(LOG_TAG,"Proximity Alert Intent Received, eventID = "+ eventID);
    }

    private Notification setNotification(Context context, String textTitle, String textContent) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.getIntentFrom(), Constants.getFromProximityIntent());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, HomeActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.getChannelId())
                .setSmallIcon(R.drawable.notification_smartparking)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
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
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        setNotificationChannel(notificationManager);
        Notification notification = setNotification(context, title, message);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(idNotification, notification);
    }

    private void startUpdatingLocation(Context context, Boolean enter){
        MyLocationListener myLocationListener = new MyLocationListener(context);
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManager.removeUpdates(myLocationListener);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(enter){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    Constants.getSecondsInMilliseconds(),
                    0,
                    myLocationListener
            );
        }else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    Constants.getMinutesInMilliseconds() * 20,
                    0,
                    myLocationListener
            );
        }

    }

}
