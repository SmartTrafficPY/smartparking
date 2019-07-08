package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.controllers.ControllerLogin;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.R;

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
        //Using this fields...

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
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (!validCredentials()) {
                            onLoginFailed();
                        }else{
                            onLoginSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    private boolean validCredentials() {
        boolean validLogin = false;

        Credentials credentials = new Credentials();

        credentials.setAlias(aliasText.getText().toString());
        credentials.setPassword(passwordText.getText().toString());

        ControllerLogin controller = new ControllerLogin();
        controller.start(credentials); //the controller get the response of the API service...

        //TODO: Here should return TRUE if credentials exists...
        return validLogin;

    }

    public void onLoginSuccess() {
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    public void onLoginFailed() {
        /**TODO: Send a Notification to the user, and open the possibility of resetPassword...**/
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            } else{
                // not good
            }
        }
    }
}
