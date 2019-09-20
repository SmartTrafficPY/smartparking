package smarttraffic.smartparking.Interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class AddCookiesInterceptor implements Interceptor {

    public static final String LOG_TAG = AddCookiesInterceptor.class.getSimpleName();

    public static final String SESSION_COOKIES = "SESSION_COOKIES";
    public static final String CSRF_COOKIES = "CSRF_COOKIES";
    public static final String CSRF_TOKEN = "X-CSRFToken";
    public static final String COOKIE = "Cookie";
    public static final String COOKIES_CLIENT = "Cookies Client";

    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                COOKIES_CLIENT, Context.MODE_PRIVATE);
        HashSet<String> csrfCookies = (HashSet<String>) sharedPreferences.getStringSet(
                CSRF_COOKIES, new HashSet<String>());
        HashSet<String> sessionCookies = (HashSet<String>) sharedPreferences.getStringSet(
                SESSION_COOKIES, new HashSet<String>());
        String cookiesConcat = new String();

        for (String cookie : csrfCookies) {
            cookiesConcat = cookie.substring(0, cookie.indexOf(";"));
            builder.header(CSRF_TOKEN, cookie.substring(10, cookie.indexOf(";")));
        }

        for (String cookie : sessionCookies) {
            cookiesConcat = cookiesConcat + "; " + cookie.substring(0, cookie.indexOf(";"));
        }

        builder.header(COOKIE, cookiesConcat);

        return chain.proceed(builder.build());
    }

}