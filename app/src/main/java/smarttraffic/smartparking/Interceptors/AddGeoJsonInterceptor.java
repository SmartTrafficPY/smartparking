package smarttraffic.smartparking.Interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import smarttraffic.smartparking.SmartParkingInitialToken;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.tokenInterceptors
 */

public class AddGeoJsonInterceptor implements Interceptor {

    public AddGeoJsonInterceptor(){
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest  = chain.request().newBuilder()
                .addHeader("Accept", "application/vnd.geo+json")
                .build();
        return chain.proceed(newRequest);
    }

}
