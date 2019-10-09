package smarttraffic.smartparking.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.Interceptors.AddUserTokenInterceptor;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class ChangePasswordActivity extends Activity {

    private static final String PASS_NOT_MATCH = "Las contraseñas no coinciden!";
    private static final String CHANGE_SUCCESS = "Cambio de contraseña exitoso!";
    private static final String CHANGE_NOT_SUCCESS = "Cambio de contraseña NO exitoso!";
    private static final String SERVER_MISTAQUE = "Las contraseña no se ha podido cambiar.";

    @BindView(R.id.changePasswordButton)
    Button changePassButton;
    @BindView(R.id.currentPassword)
    EditText currentPassword;
    @BindView(R.id.newPassword1)
    EditText firstNewPassword;
    @BindView(R.id.newPassword2)
    EditText secondNewPassword;
    @BindView(R.id.passwordNotMatch)
    TextView passwordNotMatch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);
        ButterKnife.bind(this);
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                Constants.CLIENTE_DATA, Context.MODE_PRIVATE);

        final String userPassword = sharedPreferences.getString(Constants.USER_PASS, "");

        firstNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Not needed...
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!firstNewPassword.getText().toString().equals(
                        secondNewPassword.getText().toString())){
                    passwordNotMatch.setVisibility(View.VISIBLE);
                }else{
                    passwordNotMatch.setVisibility(View.INVISIBLE);
                }
            }
        });

        secondNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Not needed...
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!firstNewPassword.getText().toString().equals(
                        secondNewPassword.getText().toString())){
                    passwordNotMatch.setVisibility(View.VISIBLE);

                }else{
                    passwordNotMatch.setVisibility(View.INVISIBLE);
                }
            }
        });

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPassword.getText().toString().equals(userPassword)){
                    changeProfileUser(sharedPreferences);
                }else{
                    showToast(PASS_NOT_MATCH);
                }
            }
        });
    }

    private void changeProfileUser(SharedPreferences sharedPreferences) {
        Credentials credentials = new Credentials();
        credentials.setPassword(firstNewPassword.getText().toString());
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddUserTokenInterceptor(this))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        int userId = sharedPreferences.getInt(Constants.USER_ID, -1);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ProfileUser> call = smartParkingAPI.updateUserProfile(userId, credentials);

        call.enqueue(new Callback<ProfileUser>() {
            @Override
            public void onResponse(Call<ProfileUser> call, Response<ProfileUser> response) {
                switch (response.code()) {
                    case 200:
                        showToast(CHANGE_SUCCESS);
                        editor.putString(Constants.USER_PASS,
                                firstNewPassword.getText().toString());
                        Intent intent = new Intent(ChangePasswordActivity.this,
                                HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case 400:
                        showToast(CHANGE_NOT_SUCCESS);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<ProfileUser> call, Throwable t) {
                t.printStackTrace();
                showToast(SERVER_MISTAQUE);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

}
