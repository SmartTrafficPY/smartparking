package smarttraffic.smartparking.receivers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import smarttraffic.smartparking.activities.LoginActivity;
import smarttraffic.smartparking.services.RegistrationService;

public class RegistrationReceiver extends BroadcastReceiver {

    private static final String TAG = "RegistrationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(RegistrationService.REGISTRATION_ACTION)) {
            Log.i(TAG,"New user receive!");
            //Basic alert Dialog....
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Registro");
            alertDialog.setMessage("Realizado con exito!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            //Go to Login...or Home?
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
        }
        else if(intent.getAction().equals(RegistrationService.BAD_REGISTRATION_ACTION)) {
            Log.i(TAG,"User profile already exists!");
            //Maybe we can get a message class to get better dialog communication with user...
            Toast.makeText(context, "Alias already taken!", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Please choose another alias!", Toast.LENGTH_LONG).show();
        }
    }
}
