package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.LoginReceiver;
import smarttraffic.smartparking.services.LoginService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // binds the elements of the login_layout
    @BindView(R.id.aliasLogin)
    EditText aliasText;
    @BindView(R.id.passwordLogin)
    EditText passwordText;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.linkSignUp)
    TextView goSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(); // function that makes the login process...
            }
        });

        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivity(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginService.LOGIN_ACTION);
        filter.addAction(LoginService.BAD_LOGIN_ACTION);
        LoginReceiver loginReceiver = new LoginReceiver();
        registerReceiver(loginReceiver, filter);
    }

    private void login() {
        Log.d(TAG, "User trying to make the login");

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verificando...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        /**Here the service get the request of Login...**/
                        sendLoginRequest();
                        loginButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    private void sendLoginRequest() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginService.class);
        loginIntent.putExtra("alias", aliasText.getText().toString());
        loginIntent.putExtra("password", passwordText.getText().toString());
        startService(loginIntent);
    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
    }

}
