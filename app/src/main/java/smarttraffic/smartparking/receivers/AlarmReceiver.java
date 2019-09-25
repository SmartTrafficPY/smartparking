package smarttraffic.smartparking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.receivers
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: add geofences here...
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
    }
}
