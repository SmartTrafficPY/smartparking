package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.cookiesInterceptor.AddCookiesInterceptor;
import smarttraffic.smartparking.cookiesInterceptor.ReceivedCookiesInterceptor;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.SmartParkingProfile;
import smarttraffic.smartparking.receivers.RegistrationReceiver;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class RegistrationService extends IntentService {

    public static final String PROBLEM = "Found some Problem in Login";
    private static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor," +
            " favor revisar conexion!";

    public static final String REGISTRATION_ACTION = "Registro correcto";
    public static final String BAD_REGISTRATION_ACTION = "Registro no realizado";

    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ProfileRegistry profileRegistry = new ProfileRegistry();
        profileRegistry.setSmartParkingProfile(new SmartParkingProfile());
        Bundle extras = intent.getExtras();
        setRegistrationExtras(extras, profileRegistry);

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
                .baseUrl(Constants.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ProfileUser> call = smartParkingAPI.signUpUser(profileRegistry);
        Intent registrationIntent = new Intent("registrationIntent");
        registrationIntent.setClass(this, RegistrationReceiver.class);

        try {
            Response<ProfileUser> result = call.execute();
            if(result.code() == 201){
                registrationIntent.setAction(REGISTRATION_ACTION);
            }else{
                registrationIntent.putExtra("exists", "Profile already exists");
                registrationIntent.setAction(BAD_REGISTRATION_ACTION);
            }
        } catch (IOException e) {
            registrationIntent.putExtra(PROBLEM, CANNOT_CONNECT_SERVER);
            registrationIntent.setAction(BAD_REGISTRATION_ACTION);
            e.printStackTrace();
        }
        sendBroadcast(registrationIntent);
    }

    private void setRegistrationExtras(Bundle extras, ProfileRegistry profileRegistry){
        profileRegistry.setUsername(extras.getString("username"));
        profileRegistry.setPassword(extras.getString("password"));
        profileRegistry.getSmartParkingProfile().setSex(extras.getString("sex"));
    }
}
