package smarttraffic.smartparking.controllers;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.activities.LoginActivity;
import smarttraffic.smartparking.apiFeed.LoginFeed;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.SmartParkingAPI;

public class ControllerLogin implements Callback<LoginFeed> {

    private static final String TAG = "LoginController";

    static final String BASE_URL = "http://10.50.225.77:8000/api/smartparking/profiles/login/";

    public void start(Credentials credentials) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);

        Call<LoginFeed> call = smartParkingAPI.loginUser(credentials);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginFeed> call, Response<LoginFeed> response) {
        /**
         *                 switch (response.code()) {
         *                     case 200:
         *                         PokemonFeed data = response.body();
         *                         view.notifyDataSetChanged(data.getResults());
         *                         break;
         *                     case 401:
         *
         *                         break;
         *                     default:
         *
         *                         break;
         *                 }
         **/
        if(response.isSuccessful()) {
            LoginFeed loginFeed = response.body();
            Log.d(TAG, loginFeed.getIdentifier());
            System.out.println(loginFeed.getIdentifier());
        } else {
            System.out.println(response.errorBody());
            Log.e(TAG, String.valueOf(response.errorBody()));
        }
    }

    @Override
    public void onFailure(Call<LoginFeed> call, Throwable t) {
        t.printStackTrace();
    }
}
