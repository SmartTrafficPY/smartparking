package smarttraffic.smartparking.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import smarttraffic.smartparking.R;

public class ResetPassActivity extends AppCompatActivity {

    /**
     * Reset Password if user Lost it...
     * **/

    private static final String TAG = "ResetPassActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
    }
}
