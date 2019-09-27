package smarttraffic.smartparking.dataModels.Spots;

import java.util.List;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class SpotList {

    private String type;
    private List<Spot> features;

    public SpotList() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "SpotList{" +
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

    public List<Spot> getFeatures() {
        return features;
    }

    public void setFeatures(List<Spot> features) {
        this.features = features;
    }

    public boolean isEmpty() {
        if(getFeatures().isEmpty()){
            return true;
        }else{
            return false;
        }
    }
}
