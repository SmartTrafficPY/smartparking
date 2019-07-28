package smarttraffic.smartparking.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import smarttraffic.smartparking.dataModels.Passwords;

public class ChangePassFragment extends Fragment {

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

    private static final String BASE_URL_HOME = "http://192.168.100.5:8000/api/smartparking/";
    private static final String BASE_URL = "http://10.50.225.75:8000/api/smartparking/";
    private static final String PASSWORDS_NOT_MATCH = "Las contraseñas no coinciden!";
    private static final String CHANGE_SUCCESS = "La contraseña se ha cambiado exitosamente!";
    private static final String CHANGE_NOT_SUCCESS = "La contraseña actual no coincide con la de su usuario!";
    private static final String SERVER_MISTAQUE = "Las contraseña no se ha podido cambiar.";
    private static final String LOG_TAG = "ChangePassFragment";

    public ChangePassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_layout, container, false);
        ButterKnife.bind(this, view);

        /**
         * Menu Fragment manege the layout, maybe this could manage the actions of it...
         * setContentView(R.layout.change_password_layout);
         * **/

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "User trying to change his password");
                if(firstNewPassword.getText().toString().
                        equals(secondNewPassword.getText().toString())){
                    Passwords passwords = new Passwords();
                    passwords.setCurrent_pass(currentPassword.getText().toString());
                    passwords.setNew_pass(firstNewPassword.getText().toString());
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(new AddCookiesInterceptor(getActivity()))
                            .addInterceptor(new ReceivedCookiesInterceptor(getActivity()))
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl(BASE_URL_HOME)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
                    Call<ResponseBody> call = smartParkingAPI.changeUserPassword(passwords);

                    call.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            switch (response.code()) {
                                case 202:
                                    showToast(CHANGE_SUCCESS);
//                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
//                                    startActivity(intent);
                                    break;
                                case 400:
                                    showToast(CHANGE_NOT_SUCCESS);
                                    break;
                                default:
                                    break;
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            showToast(SERVER_MISTAQUE);
                            Log.e(LOG_TAG,t.toString());
                        }
                    });
                }else{
                    showToast(PASSWORDS_NOT_MATCH);
                }
            }
        });

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.mipmap.toast_smartparking_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

}
