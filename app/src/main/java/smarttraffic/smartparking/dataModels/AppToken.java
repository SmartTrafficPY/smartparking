package smarttraffic.smartparking.dataModels;

import smarttraffic.smartparking.dataModels.Spots.PolygonGeometry;

/**
 * Created by Joaquin on 10/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */
public class AppToken {

    private String type;
    private TokenProperties properties;
    private PolygonGeometry geometry;

    public AppToken() {
    }

    @Override
    public String toString() {
        return "AppToken{" +
                "properties=" + properties +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PolygonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PolygonGeometry geometry) {
        this.geometry = geometry;
    }

    public TokenProperties getProperties() {
        return properties;
    }

    public void setProperties(TokenProperties properties) {
        this.properties = properties;
    }

}