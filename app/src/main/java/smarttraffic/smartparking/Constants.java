package smarttraffic.smartparking;

/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */

public class Constants {


    /**
     * Add here all variables used at width in the project...
     * **/

    public Constants() {
    }

    public static final String GEOFENCES_ADDED_KEY = "GEOFENCES_ADDED_KEY";
    private static final long SECONDS_IN_MILLISECONDS = 1000 * 1;
    private static final long MINUTES_IN_MILLISECONDS = SECONDS_IN_MILLISECONDS * 60;
    private static final long HOURS_IN_MILLISECONDS = MINUTES_IN_MILLISECONDS * 60;
    //Then here will be the server IP
    private static final String BASE_URL = "http://10.50.225.77:8000/api/smartparking/";
    private static final String BASE_URL_HOME = "http://192.168.100.5:8000/api/smartparking/";
    private static final String CHANNEL_ID = "SMARTPARKING_CHANNEL_ID";
    private static final String PROXIMITY_INTENT_ACTION = "SMARTPARKING_PROXIMITY_ALERT";
    private static final String NOTIFICATION_SERVICE = "notification";
    private static final String BROADCAST_LOCATION_INTENT = "BROADCAST_LOCATION_INTENT";
    private static final String FROM_PROXIMITY_INTENT = "FROM_PROXIMITY_INTENT";
    private static final String INTENT_FROM = "INTENT_FROM";
    private static final String LATITUD = "LATITUD";
    private static final String LONGITUD = "LONGITUD";
    private static final String RADIOUS = "RADIOUS";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long DISTANCE_CHANGE_FOR_UPDATES = 100;
    private static final long HIGH_FREQUENCY_UPDATES = SECONDS_IN_MILLISECONDS * 1;
    private static final long LOW_FREQUENCY_UPDATES = MINUTES_IN_MILLISECONDS * 20;
    private static final String PROX_ALERT_INTENT = "smarttraffic.smartparking.services.ProximityAlert";

    public static String getLatitud() {
        return LATITUD;
    }

    public static String getLongitud() {
        return LONGITUD;
    }

    public static String getRadious() {
        return RADIOUS;
    }
    public static String getProxAlertIntent() {
        return PROX_ALERT_INTENT;
    }

    public static String getFromProximityIntent() {
        return FROM_PROXIMITY_INTENT;
    }

    public static String getIntentFrom() {
        return INTENT_FROM;
    }

    public static String getBroadcastLocationIntent() {
        return BROADCAST_LOCATION_INTENT;
    }

    public static long getMinDistanceChangeForUpdates() {
        return MIN_DISTANCE_CHANGE_FOR_UPDATES;
    }

    public static long getDistanceChangeForUpdates() {
        return DISTANCE_CHANGE_FOR_UPDATES;
    }

    public static long getHighFrequencyUpdates() {
        return HIGH_FREQUENCY_UPDATES;
    }

    public static long getLowFrequencyUpdates() {
        return LOW_FREQUENCY_UPDATES;
    }


    public static long getSecondsInMilliseconds() {
        return SECONDS_IN_MILLISECONDS;
    }

    public static long getMinutesInMilliseconds() {
        return MINUTES_IN_MILLISECONDS;
    }

    public static long getHoursInMilliseconds() {
        return HOURS_IN_MILLISECONDS;
    }

    public static String getNotificationService() {
        return NOTIFICATION_SERVICE;
    }

    public static String getProximityIntentAction() {
        return PROXIMITY_INTENT_ACTION;
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getBaseUrlHome() {
        return BASE_URL_HOME;
    }

}
