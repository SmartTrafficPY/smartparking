package smarttraffic.smartparking.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import smarttraffic.smartparking.R;

public class RegistryActivity extends AppCompatActivity {

    /**
     * The user register and get a profile on the system...
     * **/

    private static final String TAG = "RegistryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registry_activity);
    }
}
