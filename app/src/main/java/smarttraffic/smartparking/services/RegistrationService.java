package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.ProfileRegistry;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.dataModels.SmartParkingProfile;
import smarttraffic.smartparking.receivers.RegistrationReceiver;
import smarttraffic.smartparking.Interceptors.AddSmartParkingTokenInterceptor;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class RegistrationService extends IntentService {

    public static final String PROBLEM = "Found some Problem in Login";
    private static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor," +
            " favor revisar conexion!";
    private static final String ALREADY_EXISTS = "El perfil utilizado ya existe!";

    public static final String REGISTRATION_OK = "Registro correcto";
    public static final String BAD_REGISTRATION = "Registro no realizado";

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
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //add the token header "Authorization"
                .addInterceptor(new AddSmartParkingTokenInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ProfileUser> call = smartParkingAPI.signUpUser(profileRegistry);
        final Intent registrationIntent = new Intent("registrationIntent");
        registrationIntent.setClass(this, RegistrationReceiver.class);

        try {
            Response<ProfileUser> result = call.execute();
            if(result.code() == 201){
                registrationIntent.setAction(REGISTRATION_OK);
            }else if(result.code() == 400){
                registrationIntent.putExtra(PROBLEM, ALREADY_EXISTS);
                registrationIntent.setAction(BAD_REGISTRATION);
            }
        } catch (IOException e) {
            registrationIntent.putExtra(PROBLEM, CANNOT_CONNECT_SERVER);
            registrationIntent.setAction(BAD_REGISTRATION);
            e.printStackTrace();
        }
        sendBroadcast(registrationIntent);
    }

    private void setRegistrationExtras(Bundle extras, ProfileRegistry profileRegistry){
        profileRegistry.setUsername(extras.getString("username"));
        profileRegistry.setPassword(extras.getString("password"));
        profileRegistry.getSmartParkingProfile().setBirth_date(extras.getString("birth_date"));
        profileRegistry.getSmartParkingProfile().setSex(extras.getString("sex"));
    }
}
