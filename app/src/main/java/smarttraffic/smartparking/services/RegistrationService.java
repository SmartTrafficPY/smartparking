package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.ProfileRegistry;

public class RegistrationService extends IntentService {

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ProfileRegistry> call = smartParkingAPI.signUpUser(profileRegistry);

        try {
            Response<ProfileRegistry> result = call.execute();
            if(result.code() == 201){
                Intent responseIntent = new Intent();
                responseIntent.setAction(REGISTRATION_ACTION);
                sendBroadcast(responseIntent);
            }else{
                Intent responseIntent = new Intent();
                responseIntent.putExtra("exists", "Profile already exists");
                responseIntent.setAction(BAD_REGISTRATION_ACTION);
                sendBroadcast(responseIntent);
            }
        } catch (IOException e) {
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
