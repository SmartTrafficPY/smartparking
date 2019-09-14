package smarttraffic.smartparking.dataModels.Lots;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class LotProperties {

    private String url;
    private float radio;
    private String name;
    private PointGeometry center;

    public LotProperties() {
    }

    @Override
    public String toString() {
        return "LotProperties{" +
                "url='" + url + '\'' +
                ", radio=" + radio +
                ", name='" + name + '\'' +
                ", center=" + center +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointGeometry getCenter() {
        return center;
    }

    public void setCenter(PointGeometry center) {
        this.center = center;
    }

    public int getIdFromUrl() {
        String[] parts = getUrl().split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

}
