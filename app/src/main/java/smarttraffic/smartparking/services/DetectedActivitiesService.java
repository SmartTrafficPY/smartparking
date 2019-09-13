package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.Utils;
import smarttraffic.smartparking.activities.HomeActivity;

public class DetectedActivitiesService extends IntentService {

    private static final String LOG_TAG = "DetectedActivities";

    public DetectedActivitiesService() {
        super("DetectedActivitiesService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(Constants.KEY_DETECTED_ACTIVITIES,
                        Utils.detectedActivitiesToJson(detectedActivities))
                .apply();

        // Log each activity.
        Log.i(LOG_TAG, Utils.getActivityString(
                getApplicationContext(),
                result.getMostProbableActivity().getType()) + " " + result.getMostProbableActivity().getConfidence() + "%");
        broadcastActivityTransition(result);
    }

    private void broadcastActivityTransition(ActivityRecognitionResult result) {
        Intent intent = new Intent(Constants.BROADCAST_TRANSITION_ACTIVITY_INTENT);
        intent.putExtra(Constants.ACTIVITY_TYPE_TRANSITION, result.getMostProbableActivity().getType());
        intent.putExtra(Constants.ACTIVITY_CONFIDENCE_TRANSITION, result.getMostProbableActivity().getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
