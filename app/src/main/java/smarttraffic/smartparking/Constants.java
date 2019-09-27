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

    private Constants() {
        // Persistence Constructor
    }

    public static final String EVENT_TYPE_EXIT = "exit_lot";
    public static final String EVENT_TYPE_ENTRACE = "enter_lot";
    public static final String HAS_ENTER_IN_LOT = "HAS_ENTER_IN_LOT";
    public static final String ENTER_LOT_FLAG = "ENTER_LOT_FLAG";
    public static final int ALARM_REQUEST_CODE=132;
    public static final String SETTINGS = "SETTINGS";
    public static final String GEOFENCES_ADD = "GEOFENCES_ADD";
    public static final String GEOFENCES_SETUP = "GEOFENCES_SETUP";
    public static final String USER_PASS = "USER_PASS";
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String CLIENT_NOT_LOGIN = "CLIENT_NOT_LOGIN";
    public static final String CLIENTE_DATA = "CLIENTE_DATA";
    public static final String USER_ID = "USER_ID";
    public static final String USER_URL = "USER_URL";
    public static final int APPLICATION_ID = 1;
    public static final String GEOFENCE_TRIGGED = "GEOFENCE_TRIGGED";
    public static final String EVENT_BASIC = "entities/smartparking/event_types/";
    public static final String GATEWAYS = "GATEWAYS";
    public static final int NOT_IN_PARKINGSPOT = -1;
    public static final String GEOFENCE_LOTS_SHARED_PREFERENCES = "GEOFENCE_LOTS_SHARED_PREFERENCES";
    private static final String BROADCAST_GEOFENCE_TRIGGER_INTENT = "BROADCAST_GEOFENCE_TRIGGER_INTENT";
    public static final String BROADCAST_TRANSITION_ACTIVITY_INTENT = "BROADCAST_TRANSITION_ACTIVITY_INTENT";
    public static final String BROADCAST_TRANSITION_LOCATION_INTENT = "BROADCAST_TRANSITION_LOCATION_INTENT";
    public static final String GEOFENCE_TRIGGER_ID = "geofenceTriggerId";
    public static final String GEOFENCE_TRANSITION_TYPE = "GEOFENCE_TRANSITION_TYPE";

    public static final String KEY_DETECTED_ACTIVITIES = "KEY_DETECTED_ACTIVITIES";
    public static final String KEY_ACTIVITY_UPDATES_REQUESTED = "KEY_ACTIVITY_UPDATES_REQUESTED";
    public static final String GEOFENCES_ADDED_KEY = "GEOFENCES_ADDED_KEY";
    private static final long SECONDS_IN_MILLISECONDS = (long) 1000 * 1;
    public static final String ACTIVITY_TYPE_TRANSITION = "ACTIVITY_TYPE_TRANSITION";
    public static final String ACTIVITY_CONFIDENCE_TRANSITION = "ACTIVITY_CONFIDENCE_TRANSITION";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS =  SECONDS_IN_MILLISECONDS; // 30 seconds
    private static final long MINUTES_IN_MILLISECONDS = SECONDS_IN_MILLISECONDS * 60;
    private static final long HOURS_IN_MILLISECONDS = MINUTES_IN_MILLISECONDS * 60;
    public static final String BASE_URL = "https://api.smarttraffic.com.py/api/";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
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
    public static final String TILE_SERVER = "https://api.smarttraffic.com.py/tile/";
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */

    public static String getUserId() {
        return USER_ID;
    }

    public static String getBroadcastGeofenceTriggerIntent(){
        return BROADCAST_GEOFENCE_TRIGGER_INTENT;
    }
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

    public static int getRequestPermissionsRequestCode() {
        return REQUEST_PERMISSIONS_REQUEST_CODE;
    }

}
