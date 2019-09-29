package smarttraffic.smartparking.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.R;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.activities
 */

public class BifurcationActivity extends Activity {

    @BindView(R.id.createAccountButton)
    Button goSignUp;
    @BindView(R.id.initSession)
    TextView goToLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bifurcation_layout);
        ButterKnife.bind(this);

        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BifurcationActivity.this, RegistryActivity.class);
                startActivity(intent);
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(BifurcationActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}
