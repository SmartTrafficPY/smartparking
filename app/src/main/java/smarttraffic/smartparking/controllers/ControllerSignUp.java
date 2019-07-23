package smarttraffic.smartparking.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.ProfileRegistry;

public class ControllerSignUp implements Callback<ProfileRegistry> {

    static final String BASE_URL = "http://10.50.225.75:8000/api/smartparking/profiles/";

    public void start(ProfileRegistry profileRegistry) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);

//        Call<ProfileRegistry> call = smartParkingAPI.signUpUser(profileRegistry);
//        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ProfileRegistry> call, Response<ProfileRegistry> response) {
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
            ProfileRegistry profileRegistry = response.body();
            System.out.println(profileRegistry.getUsername());
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<ProfileRegistry> call, Throwable t) {
        t.printStackTrace();
    }
}
