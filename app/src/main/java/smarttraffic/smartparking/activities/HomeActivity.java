package smarttraffic.smartparking.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import smarttraffic.smartparking.R;

public class HomeActivity extends AppCompatActivity {

    /**
    * IF the user is log in, can enter here...else: Login first
    * HERE should show the map with the info of parking spots status
     * Father of:
     * -About
     * -Change Pass
     * -Settings
    * */

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
    }

}
