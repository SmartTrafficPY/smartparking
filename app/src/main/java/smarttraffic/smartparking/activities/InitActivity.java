package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


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
        showInitProgress(true, INIT_APP_MESSAGE);
        //Register the Receiver...
        filter.addAction(InitService.TO_HOME);
        filter.addAction(InitService.HAVE_TO_LOGIN);
        registerReceiver(initReceiver, filter);
    }

    public void showInitProgress(final boolean show, final String message) {
        final ProgressDialog progressDialog = new ProgressDialog(InitActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (isNetworkAvailable()) {
                            progressDialog.show();
                            setWithInternetConnection(true);
                            initializeFirstActivity();
                        } else {
                            setWithInternetConnection(false);
                            showToast(getString(R.string.no_network_connection));
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);

        if (progressDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (show) {
                        progressDialog.setMessage(message);
                        progressDialog.show();
                    } else {
                        progressDialog.hide();
                    }
                }
            });
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
        //Register the Receiver...
        filter.addAction(InitService.TO_HOME);
        filter.addAction(InitService.HAVE_TO_LOGIN);
        registerReceiver(initReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(initReceiver);
    }

    private void initializeFirstActivity() {
        isUserLogged();
        finish();
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.toast_smartparking_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    private void isUserLogged() {
        Intent initService = new Intent(InitActivity.this, InitService.class);
        startService(initService);
    }

}

