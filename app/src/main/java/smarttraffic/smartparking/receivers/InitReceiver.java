package smarttraffic.smartparking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import smarttraffic.smartparking.activities.HomeActivity;
import smarttraffic.smartparking.activities.LoginActivity;
import smarttraffic.smartparking.services.InitService;

public class InitReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "InitReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(InitService.TO_HOME)) {
            Intent i = new Intent(context, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else{
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
