package smarttraffic.smartparking.dataModels;

import smarttraffic.smartparking.dataModels.Lots.PointGeometry;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class Events {

    private String type;
    private EventProperties properties;
    private PointGeometry geometry;

    public Events() {
    }

    @Override
    public String toString() {
        return "Events{" +
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

    public EventProperties getProperties() {
        return properties;
    }

    public void setProperties(EventProperties properties) {
        this.properties = properties;
    }

    public PointGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PointGeometry geometry) {
        this.geometry = geometry;
    }
}
