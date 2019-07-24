package smarttraffic.smartparking.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.cookiesInterceptor.AddCookiesInterceptor;
import smarttraffic.smartparking.cookiesInterceptor.ReceivedCookiesInterceptor;
import smarttraffic.smartparking.dataModels.ResetPassword;
import smarttraffic.smartparking.services.RegistrationService;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class ResetPassActivity extends AppCompatActivity {

    /**
     * Reset Password if user lost it...
     * **/

    private static final String LOG_TAG = "ResetPassActivity";
    private static final String CERO = "0";
    private static final String GUION = "-";
    private static final String FAIL_RESET_MESSAGE = "Algo sucedio durante el proceso de " +
            "recuperacion de la contraseña";
    static final String BASE_URL = "http://10.50.225.75:8000/api/smartparking/";

    @BindView(R.id.birthDateResetPass)
    TextView birthDate;
    @BindView(R.id.usernameResetPass)
    EditText username;
    @BindView(R.id.resetPassButton)
    Button resetPass;
    @BindView(R.id.datePickerButtonResetPass)
    Button datePickerButton;
    @BindView(R.id.maleResetPass)
    RadioButton maleRadioButton;
    @BindView(R.id.femaleResetPass)
    RadioButton femaleRadioButton;

    public final Calendar calendar = Calendar.getInstance();

    final int actuallMonth = calendar.get(Calendar.MONTH);
    final int actuallDay = calendar.get(Calendar.DAY_OF_MONTH);
    final int actuallYear = calendar.get(Calendar.YEAR);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        ButterKnife.bind(this);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                getDatePickedUp();
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUsersPassword();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getDatePickedUp() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                birthDate.setText(year + GUION + mesFormateado + GUION + diaFormateado);
            }

        }, actuallYear, actuallMonth, actuallDay);
        datePickerDialog.show();
    }

    private void resetUsersPassword() {
        Log.d(LOG_TAG, "User trying to reset password!");

        resetPass.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetPassActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Realizando las consultas pertinentes...");

        //TODO: with data introduce, consult if user exists with those data...
        sendResetPassRequest();
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        resetPass.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void sendResetPassRequest() {
        //TODO: request the DB if a user with data introduced exists...
        /**
         * @params: username, onRadioButtonClicked(), birthDate
         * **/

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor(this))
                .addInterceptor(new AddCookiesInterceptor(this))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ResponseBody> call = smartParkingAPI.canResetPass(new ResetPassword(
                username.getText().toString(),
                birthDate.getText().toString(),
                onRadioButtonClicked()));

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 202:
                        //TODO: call to show the pass?...
                        break;
                    default:
                        showToast(FAIL_RESET_MESSAGE);
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG,t.toString());
            }
        });

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

    public String onRadioButtonClicked() {
        if(femaleRadioButton.isChecked()){
            return "F";
        }else if(maleRadioButton.isChecked()){
            return "M";
        }else{
            return null;
        }
    }

}
