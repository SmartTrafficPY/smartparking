package smarttraffic.smartparking;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Interceptors.AddUserTokenInterceptor;
import smarttraffic.smartparking.dataModels.EventProperties;
import smarttraffic.smartparking.dataModels.Events;
import smarttraffic.smartparking.dataModels.Lots.Lot;
import smarttraffic.smartparking.dataModels.Lots.LotList;
import smarttraffic.smartparking.dataModels.Lots.PointGeometry;
import smarttraffic.smartparking.dataModels.Point;
import smarttraffic.smartparking.dataModels.Spots.NearbySpot.NearbySpot;
import smarttraffic.smartparking.dataModels.Spots.Spot;
import smarttraffic.smartparking.receivers.AddAlarmReceiver;
import smarttraffic.smartparking.receivers.RemoveAlarmReceiver;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class Utils {

    public static final String LOTS_SYSTEM = "Lots in the System";

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

    private Utils() {}

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */

    @SuppressLint("StringFormatInvalid")
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public static String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<DetectedActivity>>() {}.getType();
        return new Gson().toJson(detectedActivitiesList, type);
    }

    static ArrayList<DetectedActivity> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<DetectedActivity>>(){}.getType();
        ArrayList<DetectedActivity> detectedActivities = new Gson().fromJson(jsonArray, listType);
        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }
        return detectedActivities;
    }

    public static void saveLotInSharedPreferences(Context context, List<Lot> lots) {
        SharedPreferences prefs = context.getSharedPreferences(LOTS_SYSTEM,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for(Lot lot : lots){
            editor.putInt(lot.getProperties().getName(),lot.getProperties().getIdFromUrl());
            editor.apply();
        }
    }

    public static int getLotInSharedPreferences(Context context, String lotName) {
        SharedPreferences prefs = context.getSharedPreferences(LOTS_SYSTEM,
                Context.MODE_PRIVATE);
        int id = prefs.getInt(lotName, -1);
        return id;
    }

    public static void showToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    public static void setNewStateOnSpot(final Context context, boolean isParking, int spotId) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddUserTokenInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        if(isParking){
            Call<ResponseBody> call = smartParkingAPI.setOccupiedSpot(spotId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    switch (response.code()) {
                        case 200:
                            showToast(context.getResources().getString(R.string.parked_successfull), context);
                            break;
                        default:
                            showToast(context.getResources().getString(R.string.unsuccessful), context);
                            break;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }else{
            Call<ResponseBody> call = smartParkingAPI.resetFreeSpot(spotId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    switch (response.code()) {
                        case 200:
                            showToast(context.getResources().getString(R.string.free_successfull), context);
                            break;
                        default:
                            showToast(context.getResources().getString(R.string.unsuccessful), context);
                            break;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

    }

    public static void setEntranceEvent(Context context, Location location, final String eventType){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.CLIENTE_DATA,
                Context.MODE_PRIVATE);

        String userUrl = sharedPreferences.getString(Constants.USER_URL, "");
        Events events = new Events();
        EventProperties properties = new EventProperties();
        PointGeometry geometry = new PointGeometry();
        properties.setApplication(Constants.APPLICATION_ID);
        properties.setAgent(userUrl);
        properties.setE_type(Constants.BASE_URL + Constants.EVENT_BASIC + eventType);
        geometry.setPointCoordinates(location);
        events.setType("Feature");
        events.setProperties(properties);
        events.setGeometry(geometry);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddUserTokenInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ResponseBody> call = smartParkingAPI.setUserEvent("application/vnd.geo+json", events);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 201:
                        break;
                    default:
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static List<GeoPoint> spotToListOfGeoPoints(Spot spot) {
        List<GeoPoint> polygon = new ArrayList<>();
        List<Point> polygonPoints = spot.getGeometry().getPolygonPoints();
        if (polygonPoints != null) {
            for (Point point : polygonPoints) {
                polygon.add(new GeoPoint(point.getLatitud(), point.getLongitud()));
            }
        }
        return polygon;
    }

    public static List<GeoPoint> nearbySpotToListOfGeoPoints(NearbySpot spot) {
        List<GeoPoint> polygon = new ArrayList<>();
        List<Point> polygonPoints = spot.getGeometry().getPolygonPoints();
        if (polygonPoints != null) {
            for (Point point : polygonPoints) {
                polygon.add(new GeoPoint(point.getLatitud(), point.getLongitud()));
            }
        }
        return polygon;
    }

    public static List<Spot> updateSpots(HashMap<String, String> changedSpots, List<Spot> spots) {
        List<Spot> spotsUpdated = new ArrayList<>();
        if(changedSpots != null && spots != null){
            for(Map.Entry<String,String> entry : changedSpots.entrySet()){
                Spot spot = getSpotFromKey(Integer.valueOf(entry.getKey()), spots);
                if(spot != null){
                    spot.getProperties().setState(entry.getValue());
                    spotsUpdated.add(spot);
                }
            }
        }
        return spotsUpdated;
    }

    private static Spot getSpotFromKey(int key, List<Spot> spots){
        for(Spot spot : spots){
            int id = spot.getProperties().getIdFromUrl();
            if(id == key){
                return spot;
            }
        }
        return null;
    }

    public static Boolean isDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SATURDAY:
                return false;
            case Calendar.SUNDAY:
                return false;
            default:
                return true;
        }
    }

    public static void geofencesSetUp(Context context, Boolean added){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.GEOFENCES_SETUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.GEOFENCES_ADD, added).apply();
        editor.commit();
    }

    public static boolean getGeofenceStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.GEOFENCES_SETUP, Context.MODE_PRIVATE);
        boolean isGeofenceAddes = sharedPreferences.getBoolean(
                Constants.GEOFENCES_ADD, false);
        return  isGeofenceAddes;
    }

    public static GeoPoint getCentroid(List<GeoPoint> geoPoints) {
        double[] centroid = { 0.0, 0.0 };

        for (GeoPoint point : geoPoints) {
            centroid[0] += point.getLatitude();
            centroid[1] += point.getLongitude();
        }

        int totalPoints = geoPoints.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return new GeoPoint(centroid[0], centroid[1]);
    }

    public static void addAlarmsGeofencingTask(Context context){
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent addAlarmIntent = new Intent(context, AddAlarmReceiver.class);
        Intent removeAlarmIntent = new Intent(context, RemoveAlarmReceiver.class);
        PendingIntent addAlarmPendingIntent = PendingIntent.getBroadcast(context,
                Constants.ADD_ALARM_REQUEST_CODE, addAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent removeAlarmPendingIntent = PendingIntent.getBroadcast(context,
                Constants.REMOVE_ALARM_REQUEST_CODE, removeAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC, timeToRemoveGeofences().getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, removeAlarmPendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC, timeToAddGeofences().getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, addAlarmPendingIntent);
    }

    public static Calendar timeToAddGeofences(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 30);
        long milli = calendar.getTimeInMillis();
        return calendar;
    }

    public static Calendar timeToRemoveGeofences(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        long milli = calendar.getTimeInMillis();
        return calendar;
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    @SuppressLint("StringFormatInvalid")
    public static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    public static void saveListOfGateways(Context context, LotList lotList){
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.GATEWAYS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        for(Lot lot : lotList.getFeatures()){
            Gson gson = new Gson();
            if(lot.getGeometry() != null){
                String json = gson.toJson(lot.getGeometry().toLatLngList());
                prefsEditor.putString(lot.getProperties().getName(), json).apply();
                prefsEditor.commit();
            }
        }
    }

    public static List<List<LatLng>> returnListOfGateways(Context context, ArrayList<String> geofencesName){
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.GATEWAYS,
                Context.MODE_PRIVATE);
        ArrayList<List<LatLng>> lotsPolygons = new ArrayList<>();
        if(geofencesName != null){
            for(String geofenceName : geofencesName){
                Gson gson = new Gson();
                String json = mPrefs.getString(geofenceName, "");
                List<LatLng> obj = gson.fromJson(json, List.class);
                lotsPolygons.add(obj);
            }
        }
        return lotsPolygons;
    }

    public static void hasEnterLotFlag(Context context, boolean flag){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ENTER_LOT_FLAG,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.HAS_ENTER_IN_LOT, flag).apply();
        editor.commit();
    }

    public static boolean returnEnterLotFlag(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ENTER_LOT_FLAG,
                Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.HAS_ENTER_IN_LOT, false);
    }

    public static void polygonWereDraw(Context context, boolean b) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(b){
            editor.putString(Constants.FIRST_DRAW_SHAPE, Constants.POLYGON_TO_DRAW_SETTINGS).apply();
        }else{
            editor.putString(Constants.FIRST_DRAW_SHAPE, Constants.POINT_TO_DRAW_SETTINGS).apply();
        }
        editor.commit();
    }

    public static String firstDrawShape(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.FIRST_DRAW_SHAPE, Constants.POLYGON_TO_DRAW_SETTINGS);
    }

    public static void settingsHasChanged(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SETTINGS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.LOCATIONS_REQUEST_SETTINGS_CHANGE, true).apply();
        editor.commit();
    }
}