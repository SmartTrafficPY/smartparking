package smarttraffic.smartparking.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.apiFeed.LoginFeed;
import smarttraffic.smartparking.dataModels.Credentials;
import smarttraffic.smartparking.SmartParkingAPI;

public class ControllerLogin implements Callback<LoginFeed> {

    private static final String TAG = "ControllerLogin";
    private SharedPreferences sharedPref;

    static final String BASE_URL = "http://10.50.225.75:8000/smartparking/profiles/";

    public void start(Credentials credentials, Context context) {
        sharedPref = context.getSharedPreferences(String.valueOf(R.string.credentials),
                Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);

//        Call<ResponseBody> call = smartParkingAPI.loginUser(credentials);
//        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginFeed> call, Response<LoginFeed> response) {
        switch (response.code()) {
            case 200:
                LoginFeed data = response.body();
                saveLoginFeed(data);
                break;
            default:
                response.errorBody();
                break;
        }
    }

    @Override
    public void onFailure(Call<LoginFeed> call, Throwable t) {
        t.printStackTrace();
        Log.e(TAG,t.toString());
        Log.d(TAG,t.getMessage());
    }

    public void saveLoginFeed(LoginFeed loginFeed){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(String.valueOf(R.string.username_key), loginFeed.getUsername());
        editor.putString(String.valueOf(R.string.password_key), loginFeed.getPassword());
        editor.putInt(String.valueOf(R.string.id_key), loginFeed.getId());
        editor.commit();
    }
}
