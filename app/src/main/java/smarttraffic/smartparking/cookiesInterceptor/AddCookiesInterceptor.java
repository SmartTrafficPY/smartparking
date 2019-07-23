package smarttraffic.smartparking.cookiesInterceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AddCookiesInterceptor implements Interceptor {

    public static final String LOG_TAG = AddCookiesInterceptor.class.getSimpleName();

    public static final String PREF_COOKIES = "PREF_COOKIES";
    public static final String CSRF_TOKEN = "X-CSRFToken";
    public static final String COOKIE = "Cookie";
    private static final String COOKIES_CLIENT = "Cookies Client";

    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIES_CLIENT, Context.MODE_PRIVATE);
        HashSet<String> preferences = (HashSet<String>) sharedPreferences.getStringSet(PREF_COOKIES, new HashSet<String>());

        for (String cookie : preferences) {
            builder.addHeader(COOKIE, cookie);
            builder.addHeader(CSRF_TOKEN, cookie.substring(10, cookie.indexOf(";")));
            Log.v(LOG_TAG, cookie);
        }

        return chain.proceed(builder.build());
    }
}