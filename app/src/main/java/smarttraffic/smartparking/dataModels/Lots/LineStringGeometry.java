package smarttraffic.smartparking.dataModels.Lots;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class LineStringGeometry {

    private String type;
    private List<List<Double>> coordinates;

    public LineStringGeometry() {
        // Persistence Constructor
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Point> getPolygonPoints(){
        List<Point> result = new ArrayList<>();
        List<List<Double>> lineString = getCoordinates();
        if(lineString != null && !lineString.isEmpty()){
            for(List<Double> point : lineString){
                Point newPoint = new Point(point.get(1), point.get(0));
                result.add(newPoint);
            }
        }
        return result;
    }

    public List<LatLng> toLatLngList(){
        List<Point> listOfSpots = getPolygonPoints();
        List<LatLng> resultList = new ArrayList<>();
        if(listOfSpots != null && !listOfSpots.isEmpty()){
            for(Point point : listOfSpots){
                resultList.add(new LatLng(point.getLatitud(),point.getLongitud()));
            }
        }
        return resultList;
    }

}