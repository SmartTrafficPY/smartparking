package smarttraffic.smartparking.dataModels.Spots.NearbySpot;

import java.util.ArrayList;
import java.util.List;

import smarttraffic.smartparking.dataModels.Point;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels.Spots.NearbySpot
 */

public class NearbyProperties {

    private String url;
    private String state;
    private String polygon;
    private String lot;

    public NearbyProperties() {
    }

    @Override
    public String toString() {
        return "NearbyProperties{" +
                "url='" + url + '\'' +
                ", state='" + state + '\'' +
                ", polygon='" + polygon + '\'' +
                ", lot='" + lot + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPolygon() {
        return polygon;
    }

    public void setPolygon(String polygon) {
        this.polygon = polygon;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public int getIdFromUrl() {
        String[] parts = this.url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    public int getLotId() {
        String[] parts = this.lot.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    public List<Point> getPolygonPoints() {
        List<Point> resultPoints = new ArrayList<>();
        String[] parts = getPolygon().split(" ", 2);
        String[] points= parts[1].split(",");
        resultPoints.add(stringToPolygon(points[0].substring(2)));
        for(int i = 1; i < points.length - 1; i++){
            resultPoints.add(stringToPolygon(points[i]));
        }
        resultPoints.add(stringToPolygon(points[4].substring(0, points[4].length() - 2)));
        return resultPoints;
    }

    public Point stringToPolygon(String point){
        String[] parts = point.split(" ");
        Point returnPoint = new Point(Double.valueOf(parts[1]), Double.valueOf(parts[0]));
        return returnPoint;
    }

}