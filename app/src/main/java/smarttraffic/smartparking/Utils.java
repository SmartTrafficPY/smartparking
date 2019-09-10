package smarttraffic.smartparking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import smarttraffic.smartparking.dataModels.Lots.Lot;
import smarttraffic.smartparking.dataModels.Lots.LotList;

public class Utils {

    public static final String LOTS_SYSTEM = "Lots in the System";

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

}