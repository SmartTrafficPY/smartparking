package smarttraffic.smartparking.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.LoginReceiver;
import smarttraffic.smartparking.services.LoginService;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LoginActivity";

    // binds the elements of the login_layout
    @BindView(R.id.usernameLogin)
    EditText usernameText;
    @BindView(R.id.passwordLogin)
    EditText passwordText;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.linkSignUp)
    TextView goSignUp;
    @BindView(R.id.forgotPassword)
    TextView forgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login_layout);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCredentialsInput()){
                    login();
                    // function that makes the login process...
                }
            }
        });

        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                startActivity(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginService.LOGIN_ACTION);
        filter.addAction(LoginService.BAD_LOGIN_ACTION);
        LoginReceiver loginReceiver = new LoginReceiver();
        registerReceiver(loginReceiver, filter);

        Intent intent = getIntent();
        String statusRegistry = intent.getStringExtra("status_registro");
        if(statusRegistry != null){
            showToast(statusRegistry);
        }
    }

    private void login() {
        Log.d(LOG_TAG, "User trying to make the login");

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verificando...");
        progressDialog.show();
        sendLoginRequest();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        /**Here the service get the request of Login...**/
                        loginButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void sendLoginRequest() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginService.class);
        loginIntent.putExtra("username", usernameText.getText().toString());
        loginIntent.putExtra("password", passwordText.getText().toString());
        startService(loginIntent);
    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
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

    private boolean checkCredentialsInput(){
        if(usernameText.getText().toString() != null){
            if(passwordText.getText().toString().length() > 5){
                return true;
            }else{
                showToast("La CONTRASEÑA debe tener al menos 6 caracteres!");
                return false;
            }
        }else{
            showToast("El ALIAS no puede estar vacio!");
            return false;
        }
    }

}
