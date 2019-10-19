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
import smarttraffic.smartparking.Utils;

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
                        initializeFirstActivity(sharedPreferences);
                        progressDialog.dismiss();
                    }
                }, Constants.getSecondsInMilliseconds());
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

}

