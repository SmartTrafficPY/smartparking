package smarttraffic.smartparking.dataModels.Spots;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class Spot {

    private String type;
    private SpotProperties properties;
    private PolygonGeometry geometry;

    public Spot() {
    }

    @Override
    public String toString() {
        return "Spot{" +
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

    public SpotProperties getProperties() {
        return properties;
    }

    public void setProperties(SpotProperties properties) {
        this.properties = properties;
    }

    public PolygonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PolygonGeometry geometry) {
        this.geometry = geometry;
    }

    public List<LatLng> toLatLngList(){
        List<Point> listOfSpots = getGeometry().getPolygonPoints();
        List<LatLng> resultList = new ArrayList<LatLng>();
        if(listOfSpots != null){
            for(Point point : listOfSpots){
                resultList.add(new LatLng(point.getLatitud(),point.getLongitud()));
            }
        }
        return resultList;
    }
}
