package smarttraffic.smartparking.Interceptors;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import smarttraffic.smartparking.Constants;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.tokenInterceptors
 */

public class AddUserTokenInterceptor implements Interceptor {

    private Context context;

    public AddUserTokenInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CLIENTE_DATA, Context.MODE_PRIVATE);
        String userToken = sharedPreferences.getString(Constants.USER_TOKEN,
                Constants.CLIENT_NOT_LOGIN);

        Request newRequest  = chain.request().newBuilder()
                .addHeader("Authorization", "Token "
                        + userToken)
                .build();
        return chain.proceed(newRequest);
    }
}

