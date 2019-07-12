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
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.ProfileRegistry;

public class RegistrationService extends IntentService {

    public static final String PROBLEM = "Found some Problem in Login";
    private static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor," +
            " favor revisar conexion!";

    static final String BASE_URL = "http://10.50.225.77:8000/smartparking/";

    public static final String REGISTRATION_ACTION = "Registro correcto";
    public static final String BAD_REGISTRATION_ACTION = "Registro no realizado";

    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ProfileRegistry profileRegistry = new ProfileRegistry();
        Bundle extras = intent.getExtras();
        setRegistrationExtras(extras, profileRegistry);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ProfileRegistry> call = smartParkingAPI.signUpUser(profileRegistry);

        try {
            Response<ProfileRegistry> result = call.execute();
            if(result.code() == 201){
                Intent resultIntent = new Intent();
                resultIntent.setAction(REGISTRATION_ACTION);
                sendBroadcast(resultIntent);
            }else{
                Intent errorIntent = new Intent();
                errorIntent.putExtra("exists", "Profile already exists");
                errorIntent.setAction(BAD_REGISTRATION_ACTION);
                sendBroadcast(errorIntent);
            }
        } catch (IOException e) {
            Intent responseIntent = new Intent();
            responseIntent.putExtra(PROBLEM, CANNOT_CONNECT_SERVER);
            responseIntent.setAction(BAD_REGISTRATION_ACTION);
            sendBroadcast(responseIntent);
            e.printStackTrace();
        }

    }

    private void setRegistrationExtras(Bundle extras, ProfileRegistry profileRegistry){
        profileRegistry.setAge(extras.getInt("age"));
        profileRegistry.setAlias(extras.getString("alias"));
        profileRegistry.setPassword(extras.getString("password"));
        profileRegistry.setSex(extras.getString("sex"));
    }
}
