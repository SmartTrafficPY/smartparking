package smarttraffic.smartparking.Interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import smarttraffic.smartparking.SmartParkingInitialData;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.tokenInterceptors
 */

public class AddSmartParkingTokenInterceptor implements Interceptor {

    public AddSmartParkingTokenInterceptor(){
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest  = chain.request().newBuilder()
                .addHeader("Authorization", "Token "
                        + SmartParkingInitialData.getToken())
                .build();
        return chain.proceed(newRequest);
    }
}
