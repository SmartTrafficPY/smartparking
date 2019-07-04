package smarttraffic.smartparking;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import retrofit2.http.GET;
import retrofit2.Call;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // binds the elements of the login_layout
    @BindView(R.id.aliasLogin)
    EditText aliasText;
    @BindView(R.id.passwordLogin)
    EditText passwordText;
    @BindView(R.id.loginButton)
    Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        //Using this fields...

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(); // function that makes the login process...
            }
        });
    }

    private void login() {
        Log.d(TAG, "Trying to make the login");

        if (!validCredentials()) {
            onLoginFailed();
            return;
        }

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
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);


    }

    private boolean validCredentials() {
        boolean validLogin = false;
        //TODO: connect to the Django-sever to authenticate this credentials...

        //contact with server and return an ID if credentials exists...
        //...or a message of not found tuple(alias, password)

        String alias = aliasText.getText().toString();
        String password = passwordText.getText().toString();


        return validLogin;

    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // disable going back...
        moveTaskToBack(true);
    }


}
