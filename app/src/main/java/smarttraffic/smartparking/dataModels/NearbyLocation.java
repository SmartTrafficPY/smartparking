package smarttraffic.smartparking.dataModels;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class NearbyLocation {

    private NearbyPoint point;
    private String previous_timestamp;

    public NearbyLocation() {
    }

    @Override
    public String toString() {
        return "NearbyLocation{" +
                "point=" + point +
                ", previous_timestamp=" + previous_timestamp +
                '}';
    }

    public NearbyPoint getPoint() {
        return point;
    }

    public void setPoint(NearbyPoint point) {
        this.point = point;
    }

    public String getPrevious_timestamp() {
        return previous_timestamp;
    }

    public void setPrevious_timestamp(String previous_timestamp) {
        this.previous_timestamp = previous_timestamp;
    }
}

