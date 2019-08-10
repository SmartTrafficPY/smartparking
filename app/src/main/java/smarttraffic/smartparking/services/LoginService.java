package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.cookiesInterceptor.AddCookiesInterceptor;
import smarttraffic.smartparking.cookiesInterceptor.ReceivedCookiesInterceptor;
import smarttraffic.smartparking.dataModels.ProfileUser;
import smarttraffic.smartparking.receivers.LoginReceiver;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class LoginService extends IntentService {

    public static final String PROBLEM = "Ha fallado el proceso de ingreso!";
    public static final String CANNOT_LOGIN = "No se logro hacer inicio. Revisar credenciales!";
    public static final String CANNOT_CONNECT_SERVER = "No se pudo conectar con el servidor, favor revisar conexion!";
    public static final String ULI = "User Login Information";
    public static final String IULI = "IDENTIFICADOR USUARIO LOGGED IN";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    public LoginService() {
        super("LoginService");
    }

    public static final String LOGIN_ACTION = "Login exitoso!";
    public static final String BAD_LOGIN_ACTION = "Credenciales incorrectas";
    public static final String SERVER_PROBLEM = "Existe un error con la comunicacion con el servidor!";

    @Override
    protected void onHandleIntent(Intent intent) {
        String pass = intent.getStringExtra("password");
        String user = intent.getStringExtra("username");

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

        SharedPreferences sharedPreferences = this.getSharedPreferences(ULI, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.getBaseUrlHome())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        RequestBody username = (RequestBody) RequestBody.create(MediaType.parse("form-data"), user);
        RequestBody password = (RequestBody) RequestBody.create(MediaType.parse("form-data"), pass);
        Call<ProfileUser> call = smartParkingAPI.logginUser(username, password);
        Intent loginIntent = new Intent("loginIntent");
        loginIntent.setClass(this, LoginReceiver.class);

        try{
            Response<ProfileUser> result = call.execute();
            if (result.code() == 200){
                loginIntent.setAction(LOGIN_ACTION);
                editor.putInt(IULI, result.body().getId()).apply();
                editor.commit();
            }else if (result.code() == 404){
                loginIntent.putExtra(PROBLEM, CANNOT_LOGIN);
                loginIntent.setAction(BAD_LOGIN_ACTION);
            }
            else {
                loginIntent.putExtra(PROBLEM, SERVER_PROBLEM);
                loginIntent.setAction(SERVER_PROBLEM);
            }
        } catch (IOException e) {
            loginIntent.putExtra(PROBLEM, CANNOT_CONNECT_SERVER);
            loginIntent.setAction(BAD_LOGIN_ACTION);
            e.printStackTrace();
        }
        sendBroadcast(loginIntent);
    }
}
