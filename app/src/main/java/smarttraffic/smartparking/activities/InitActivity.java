package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.InitReceiver;
import smarttraffic.smartparking.services.InitService;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class InitActivity extends AppCompatActivity {

    /**
    * This is the first Activity:
    * check if the user have connection first and then
    * check if the user have a open session or not...
     * Father of:
     * -Login
     * -Home
    **/

    private boolean withInternetConnection;
    private static final String INIT_APP_MESSAGE = "Inicializando la aplicación...";

    IntentFilter filter = new IntentFilter();
    InitReceiver initReceiver = new InitReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_layout);
        showInitProgress(INIT_APP_MESSAGE);
        //Register the Receiver...
        filter.addAction(InitService.TO_HOME);
        filter.addAction(InitService.HAVE_TO_LOGIN);
    }

    public void showInitProgress(String message) {
        final ProgressDialog progressDialog = new ProgressDialog(InitActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);

        final Handler handler = new Handler();
        final long delay = Constants.getSecondsInMilliseconds() * 3;
        Runnable cronJob = new Runnable() {
            public void run() {
                progressDialog.show();
            }
        };

        if (isNetworkAvailable()) {
            initializeFirstActivity();
            setWithInternetConnection(true);
            handler.postDelayed(cronJob, delay);
        } else {
            progressDialog.dismiss();
            setWithInternetConnection(false);
            showToast(getString(R.string.no_network_connection));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setWithInternetConnection(boolean withInternetConnection) {
        this.withInternetConnection = withInternetConnection;
    }

    public boolean isWithInternetConnection() {
        return withInternetConnection;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(initReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(initReceiver);
    }

    private void initializeFirstActivity() {
        isUserLogged();
//        finish();
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    private void isUserLogged() {
        Intent initService = new Intent(InitActivity.this, InitService.class);
        startService(initService);
    }
}

