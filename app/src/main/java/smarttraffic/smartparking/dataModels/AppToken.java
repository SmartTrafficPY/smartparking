package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 10/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */
public class AppToken {

    private final static String type = "Feature";
    private TokenProperties properties;
    private final static String geometry = null;

    public AppToken(TokenProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "AppToken{" +
                "properties=" + properties +
                '}';
    }

    public static String getType() {
        return type;
    }

    public TokenProperties getProperties() {
        return properties;
    }

    public void setProperties(TokenProperties properties) {
        this.properties = properties;
    }

    public static String getGeometry() {
        return geometry;
    }
}