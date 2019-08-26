package smarttraffic.smartparking.services;

import android.app.IntentService;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.cookiesInterceptor.AddCookiesInterceptor;
import smarttraffic.smartparking.cookiesInterceptor.ReceivedCookiesInterceptor;
import smarttraffic.smartparking.receivers.InitReceiver;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class InitService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    public InitService() {
        super("InitService");
    }

    public static final String PROBLEM = "Usuario no encontrado!";
    public static final String HAVE_TO_LOGIN = "El usuario no esta loggeado";
    public static final String TO_HOME = "El usuario ya esta registrado";
    public static final String BAD_ACTION = "Inicio incorrecto!";
    public static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor, favor revisar conexion!";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient initClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor(this))
                .addInterceptor(new AddCookiesInterceptor(this))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(initClient)
                .baseUrl(Constants.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<ResponseBody> call = smartParkingAPI.isUserLogged();

        Intent initIntent = new Intent("initIntent");
        initIntent.setClass(this, InitReceiver.class);

        try{
            Response<ResponseBody> result = call.execute();
            if (result.code() == 200){
                initIntent.setAction(TO_HOME);
            }else {
                initIntent.setAction(HAVE_TO_LOGIN);
                initIntent.putExtra(PROBLEM, HAVE_TO_LOGIN);
            }
        } catch (IOException e) {
            initIntent.putExtra(PROBLEM, CANNOT_CONNECT_SERVER);
            initIntent.setAction(BAD_ACTION);
            e.printStackTrace();
        }
        sendBroadcast(initIntent);
    }

}
