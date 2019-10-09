package smarttraffic.smartparking.dataModels.Spots.NearbySpot;

import java.util.List;

import smarttraffic.smartparking.dataModels.Spots.Spot;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels.Spots.NearbySpot
 */

public class NearbySpotList {
    private String type;
    private List<NearbySpot> features;

    public NearbySpotList() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "NearbySpotList{" +
                "type='" + type + '\'' +
                ", features=" + features +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<NearbySpot> getFeatures() {
        return features;
    }

    public void setFeatures(List<NearbySpot> features) {
        this.features = features;
    }

}
