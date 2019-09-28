package smarttraffic.smartparking.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                Constants.CLIENTE_DATA, Context.MODE_PRIVATE);

        final ProgressDialog progressDialog = new ProgressDialog(InitActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Inicializando aplicación...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (isNetworkAvailable()) {
                            initializeFirstActivity(sharedPreferences);
                        } else {
                            showToast(getString(R.string.no_network_connection));
                        }
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initializeFirstActivity(SharedPreferences sharedPreferences) {
        String userToken = sharedPreferences.getString(Constants.USER_TOKEN,
                Constants.CLIENT_NOT_LOGIN);
        if(userToken.equals(Constants.CLIENT_NOT_LOGIN)){
            Intent registration = new Intent(InitActivity.this,
                    BifurcationActivity.class);
            startActivity(registration);
        }else{
            Intent registration = new Intent(InitActivity.this,
                    HomeActivity.class);
            startActivity(registration);
        }
        finish();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

}

