package smarttraffic.smartparking.dataModels.Lots;

import java.util.List;

import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class PointGeometry {

    private String type;
    private List<Double> coordinates;

    public PointGeometry() {
    }

    @Override
    public String toString() {
        return "PointGeometry{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Point getCenterPoint(){
        if(getCoordinates() != null){
            Point centerPoint = new Point(getCoordinates().get(0), getCoordinates().get(1));
            return centerPoint;
        }
        return null;
    }
}
