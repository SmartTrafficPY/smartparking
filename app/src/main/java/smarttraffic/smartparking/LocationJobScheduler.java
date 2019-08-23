package smarttraffic.smartparking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import smarttraffic.smartparking.activities.HomeActivity;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking
 */

public class LocationJobScheduler extends JobService implements LocationListener {

    private LocationManager locationManager;
    Location location = new Location("LocationManager");
    Location pointOfInterest = new Location("jobScheduler");
    private static final String LOG_TAG = "LocationJobScheduler";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(LOG_TAG, "Job started");
        Toast.makeText(this,"Job started", Toast.LENGTH_LONG).show();

        PersistableBundle extras = params.getExtras();
        final double radio = extras.getDouble(Constants.getRadious());
        location.setLatitude(extras.getDouble(Constants.getLatitud()));
        location.setLongitude(extras.getDouble(Constants.getLongitud()));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, Constants.getMinutesInMilliseconds() * 10,
                Constants.getMinDistanceChangeForUpdates(), this);
        if (location != null) {
            if (location.distanceTo(pointOfInterest) <= radio) {
                sendNotification(getApplicationContext() ,1, "SmartParking",
                        "You are near a check parking spots");
                Toast.makeText(getBaseContext(), "Entering Proximity by service",
                        Toast.LENGTH_LONG).show();
            }
        }
        Log.d(LOG_TAG, "Job finished");
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
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

    private void checkIfNearALot(final JobParameters params) {
        PersistableBundle extras = params.getExtras();
        final double radio = extras.getDouble(Constants.getRadious());
        location.setLatitude(extras.getDouble(Constants.getLatitud()));
        location.setLongitude(extras.getDouble(Constants.getLongitud()));

//        HandlerThread handlerThread = new HandlerThread("Location Thread");
//        Handler handler = new Handler(handlerThread.getLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 * Lets check here if is location
//                 * **/
//
//                jobFinished(params, true);
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (location != null) {
                    if (location.distanceTo(pointOfInterest) <= radio) {
                        sendNotification(getApplicationContext() ,1, "SmartParking",
                                "You are near a check parking spots");                        Toast.makeText(getBaseContext(), "Entering Proximity by service",
                                Toast.LENGTH_LONG).show();
                    }
                }
                Log.d(LOG_TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void setLocation(Location location) {
        if (location != null) {
            this.location = location;
        } else {
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
            this.location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }
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
}
