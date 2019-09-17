package smarttraffic.smartparking;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking
 */

public class SmartParkingInitialData {

    private static final String token =
            "cfa35c38a0967eba4c9f7869a50652689991192d";

    private static final String tileServerUsername = "smartparking0";

    private static final String tileServerPassword = "phobicflower934";

    private static final String credentials = getTileServerUsername() + ":" +
            getTileServerPassword();

    public static String getCredentials() {
        return credentials;
    }

    public static String getTileServerUsername() {
        return tileServerUsername;
    }

    public static String getTileServerPassword() {
        return tileServerPassword;
    }

    public static String getToken() {
        return token;
    }
}
