package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import smarttraffic.smartparking.R;


public class InitActivity extends AppCompatActivity {

    /**
    * This is the first Activity:
    * check if the user have connection first and then
    * check if the user have a open session or not...
     * Father of:
     * -Login
     * -Home
    **/

    public static final String LOG_TAG = InitActivity.class.getSimpleName();
    private boolean withInternetConnection;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity);
        showProgress(true, "Inicializando la aplicación...");
        if (isNetworkAvailable()) {
            setWithInternetConnection(true);
            //Call Login API...
            //Get userData...
            initializeFirstActivity();
        } else {
            setWithInternetConnection(false);
            Toast.makeText(this,getString(R.string.no_network_connection),Toast.LENGTH_LONG);
        }
    }

    public void showProgress(final boolean show, final String message) {
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
    }

    private void initializeFirstActivity() {
        int splashTimeOut = 3000;
        //TODO: get currentUser session data saved in SharedPreferences...
////        User currentUser = new User();
//        final Intent i;
//        if (currentUser == null) {
//            i = new Intent(InitActivity.this, LoginActivity.class);
//        } else {
//            i = new Intent(InitActivity.this, HomeActivity.class);
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(i);
//                finish();
//            }
//        }, splashTimeOut);
        Intent i = new Intent(InitActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}
