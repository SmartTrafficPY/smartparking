package smarttraffic.smartparking.dataModels.Spots;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class PolygonGeometry{

    private String type;
    private List<List<List<Double>>> coordinates;

    public PolygonGeometry() {
    }

    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Point> getPolygonPoints(){
        List<Point> result = new ArrayList<>();
        List<List<Double>> cordenadas = getCoordinates().get(0);
         if(cordenadas != null){
             for(List<Double> point : cordenadas){
                 Point newPoint = new Point(point.get(1), point.get(0));
                 result.add(newPoint);
             }
         }
        return result;
    }

    public List<LatLng> toLatLngList(){
        List<Point> listOfSpots = getPolygonPoints();
        List<LatLng> resultList = new ArrayList<LatLng>();
        if(listOfSpots != null){
            for(Point point : listOfSpots){
                resultList.add(new LatLng(point.getLatitud(),point.getLongitud()));
            }
        }
        return resultList;
    }

}
