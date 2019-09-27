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

    // binds the elements of the login_layout
    @BindView(R.id.usernameLogin)
    EditText usernameText;
    @BindView(R.id.passwordLogin)
    EditText passwordText;
    @BindView(R.id.loginButton)
    Button loginButton;

    IntentFilter filter = new IntentFilter();
    LoginReceiver loginReceiver = new LoginReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login_layout);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCredentialsInput()){
                    makeLoginHappen();
                    // function that makes the makeLoginHappen process...
                }
            }
        });

        filter.addAction(LoginService.LOGIN_ACTION);
        filter.addAction(LoginService.BAD_LOGIN_ACTION);
        registerReceiver(loginReceiver, filter);

        Intent intent = getIntent();
        String statusRegistry = intent.getStringExtra("status_registro");
        if(statusRegistry != null){
            showToast(statusRegistry);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        filter.addAction(LoginService.LOGIN_ACTION);
        filter.addAction(LoginService.BAD_LOGIN_ACTION);
        registerReceiver(loginReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(loginReceiver);
    }

    private void makeLoginHappen() {
        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verificando...");
        sendLoginRequest();
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        /**Here the service get the request of Login...**/
                        loginButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }, 2000);
        eraseCredentials();
    }

    private void eraseCredentials() {
        usernameText.setText("");
        passwordText.setText("");
    }

    private void sendLoginRequest() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginService.class);
        loginIntent.putExtra("username", usernameText.getText().toString());
        loginIntent.putExtra("password", passwordText.getText().toString());
        startService(loginIntent);
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    private boolean checkCredentialsInput(){
        if(usernameText.getText().toString() != null){
            return true;
        }else{
            showToast("El USERNAME no puede estar vacio!");
            return false;
        }
    }

}
