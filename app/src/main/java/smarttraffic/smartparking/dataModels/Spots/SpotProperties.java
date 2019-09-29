package smarttraffic.smartparking.dataModels.Spots;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class SpotProperties {

    private String url;
    private String state;
    private String lot;

    public SpotProperties() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "SpotProperties{" +
                "url='" + url + '\'' +
                ", state='" + state + '\'' +
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

}
