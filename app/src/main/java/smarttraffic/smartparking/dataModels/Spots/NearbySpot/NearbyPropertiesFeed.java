package smarttraffic.smartparking.dataModels.Spots.NearbySpot;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels.Spots.NearbySpot
 */

public class NearbyPropertiesFeed {

    private String previous_timestamp;

    public NearbyPropertiesFeed() {
    }

    @Override
    public String toString() {
        return "NearbyPropertiesFeed{" +
                ", previous_timestamp='" + previous_timestamp + '\'' +
                '}';
    }

    public String getPrevious_timestamp() {
        return previous_timestamp;
    }

    public void setPrevious_timestamp(String previous_timestamp) {
        this.previous_timestamp = previous_timestamp;
    }
}
