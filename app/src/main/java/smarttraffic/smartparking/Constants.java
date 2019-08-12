package smarttraffic.smartparking;

/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */


public class Constants {

    /**
     * Add here all variables used at width in the project...
     * **/

    public Constants() {
    }

    //Then here will be the server IP
    private static final String BASE_URL = "http://10.50.225.77:8000/api/smartparking/";
    private static final String BASE_URL_HOME = "http://192.168.100.5:8000/api/smartparking/";
    private static final String CHANNEL_ID = "SMARTPARKING_CHANNEL_ID";

    public static String getChannelId() {
        return CHANNEL_ID;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getBaseUrlHome() {
        return BASE_URL_HOME;
    }

}
