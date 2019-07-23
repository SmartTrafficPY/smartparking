package smarttraffic.smartparking.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import smarttraffic.smartparking.R;
import smarttraffic.smartparking.activities.LoginActivity;
import smarttraffic.smartparking.services.RegistrationService;

public class RegistrationReceiver extends BroadcastReceiver {

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String errorMessage;


    private static final String TAG = "RegistrationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(RegistrationService.REGISTRATION_ACTION)) {
            Log.i(TAG,"New user receive!");
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("status_registro", "Registro exitoso!");
            context.startActivity(i);
        }
        else if(intent.getAction().equals(RegistrationService.BAD_REGISTRATION_ACTION)) {
            setErrorMessage(intent.getStringExtra(RegistrationService.PROBLEM));
            showToast(getErrorMessage(),context);
        }
    }
    // Show images in Toast prompt.
    @SuppressLint("ResourceAsColor")
    private void showToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.toast_smartparking);
        toastContentView.addView(imageView, 0);
        toast.show();
    }
}
