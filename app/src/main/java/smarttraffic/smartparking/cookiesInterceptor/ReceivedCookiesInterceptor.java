package smarttraffic.smartparking.cookiesInterceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    public static final String CSRF_COOKIES = "CSRF_COOKIES";
    public static final String SESSION_COOKIES = "SESSION_COOKIES";
    public static final String SET_COOKIE = "Set-Cookie";

    public static final String LOG_TAG = ReceivedCookiesInterceptor.class.getSimpleName();
    private static final String COOKIES_CLIENT = "Cookies Client";

    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers(SET_COOKIE).isEmpty()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIES_CLIENT, Context.MODE_PRIVATE);
            HashSet<String> sessionCookies = new HashSet<String>();
            HashSet<String> csrfCookies = new HashSet<String>();

            for (String header : originalResponse.headers(SET_COOKIE)) {
                if(header.startsWith("sessionid")){
                    sessionCookies.add(header);
                }else{
                    csrfCookies.add(header);
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(CSRF_COOKIES, csrfCookies).apply();
            editor.putStringSet(SESSION_COOKIES, sessionCookies).apply();
            editor.commit();

            Log.v(LOG_TAG, originalResponse.headers().get(SET_COOKIE));

        }

        return originalResponse;
    }
}
