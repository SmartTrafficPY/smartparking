package smarttraffic.smartparking.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import smarttraffic.smartparking.R;

public class ChangePassActivity extends AppCompatActivity {

    /**
        The user can change his password...
     * */

    private static final String TAG = "ChangePassActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
    }
}
