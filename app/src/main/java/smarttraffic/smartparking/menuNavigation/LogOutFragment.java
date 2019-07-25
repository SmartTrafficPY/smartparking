package smarttraffic.smartparking.menuNavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.activities.LoginActivity;
import smarttraffic.smartparking.cookiesInterceptor.AddCookiesInterceptor;
import smarttraffic.smartparking.cookiesInterceptor.ReceivedCookiesInterceptor;

public class LogOutFragment extends Fragment {

    private static final String LOG_TAG = "LogOutFragment";
    static final String BASE_URL = "http://10.50.225.75:8000/admin/";

    public LogOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.logout_smartparking);
        builder.setMessage(R.string.dialog_logout)
                .setPositiveButton(R.string.button_afirmative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: MAKE LOGOUT CALL AND RETURN TO LOGIN ACTIVITY...
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .connectTimeout(5, TimeUnit.SECONDS)
                                .writeTimeout(20, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .addInterceptor(new AddCookiesInterceptor(context))
                                .addInterceptor(new ReceivedCookiesInterceptor(context))
                                .build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .client(okHttpClient)
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();
                        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
                        Call<ResponseBody> call = smartParkingAPI.logoutUser();

                        call.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                switch (response.code()) {
                                    case 200:
                                        //TODO: Delete shared Preferences...
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                                Log.e(LOG_TAG,t.toString());
                            }
                        });
                        /**TO HERE...async call**/
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: close de dialog and go to...?
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_layout, container, false);
    }
}
