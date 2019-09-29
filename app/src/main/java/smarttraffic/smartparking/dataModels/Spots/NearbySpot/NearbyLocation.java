package smarttraffic.smartparking.dataModels.Spots.NearbySpot;

import smarttraffic.smartparking.dataModels.Lots.PointGeometry;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class NearbyLocation {

    private String type = "Feature";
    private NearbyPropertiesFeed properties;
    private PointGeometry geometry;

    public NearbyLocation() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "NearbyLocation{" +
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

    public NearbyPropertiesFeed getProperties() {
        return properties;
    }

    public void setProperties(NearbyPropertiesFeed properties) {
        this.properties = properties;
    }

    public PointGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PointGeometry geometry) {
        this.geometry = geometry;
    }
}

