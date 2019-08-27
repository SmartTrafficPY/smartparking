package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.SmartParkingLot;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking.services
 */
public class LotsService extends IntentService {

    public LotsService() {
        super("LotsService");
    }

    private static final String LOG_TAG = "LotsService";

    public static final String PROBLEM = "";
    public static final String HAVE_TO_LOGIN = "El usuario no esta loggeado";
    public static final String TO_HOME = "El usuario ya esta registrado";
    public static final String BAD_ACTION = "Inicio incorrecto!";
    public static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor, favor revisar conexion!";

    @Override
    protected void onHandleIntent(Intent intent) {
        final Location location = new Location("serverProvider");
        ArrayList parkingLots = new ArrayList<>();

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
                .baseUrl("http://192.168.100.5:8000/smartparking/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<List<SmartParkingLot>> call = smartParkingAPI.getAllLots();

        call.enqueue(new Callback<List<SmartParkingLot>>() {
            @Override
            public void onResponse(Call<List<SmartParkingLot>> call, Response<List<SmartParkingLot>> response) {
                switch (response.code()) {
                    case 200:
                        List<SmartParkingLot> lots = (List<SmartParkingLot>) call.request().body();
                        for(SmartParkingLot lot : lots){
                            location.setLatitude(lot.getLatitud_center());
                            location.setLongitude(lot.getLongitud_center());
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<SmartParkingLot>> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG,t.toString());
            }
        });

    }
}
