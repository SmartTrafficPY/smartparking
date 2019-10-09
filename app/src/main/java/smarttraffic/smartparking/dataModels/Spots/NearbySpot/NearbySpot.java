package smarttraffic.smartparking.dataModels.Spots.NearbySpot;

import smarttraffic.smartparking.dataModels.Spots.PolygonGeometry;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels.Spots.NearbySpot
 */

public class NearbySpot {

    private String type;
    private NearbyProperties properties;
    private PolygonGeometry geometry;

    public NearbySpot() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "NearbySpot{" +
                "type='" + type + '\'' +
                ", properties=" + properties +
                ", geometry=" + geometry +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NearbyProperties getProperties() {
        return properties;
    }

    public void setProperties(NearbyProperties properties) {
        this.properties = properties;
    }

    public PolygonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PolygonGeometry geometry) {
        this.geometry = geometry;
    }
}
