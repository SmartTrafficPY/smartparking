package smarttraffic.smartparking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */

public class ProximityAlert extends BroadcastReceiver{

    private static final String LOG_TAG = "ProximityAlert";
    public static final String EVENT_ID_INTENT_EXTRA = "EventIDIntentExtraKey";
    /**
     * Here should send the notification...
     * **/

    @Override
    public void onReceive(Context context, Intent intent) {
        long eventID = intent.getLongExtra(EVENT_ID_INTENT_EXTRA, -1);
        Log.v(LOG_TAG,"Proximity Alert Intent Received, eventID = "+eventID);
    }
}
