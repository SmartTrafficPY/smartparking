package smarttraffic.smartparking.dataModels.Lots;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class Lot {

    private String type;
    private LotProperties properties;
    private PointGeometry geometry;

    public Lot() {
    }

    @Override
    public String toString() {
        return "Lot{" +
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

    public LotProperties getProperties() {
        return properties;
    }

    public void setProperties(LotProperties properties) {
        this.properties = properties;
    }

    public PointGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PointGeometry geometry) {
        this.geometry = geometry;
    }
}
