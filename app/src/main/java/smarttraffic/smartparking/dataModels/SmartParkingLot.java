package smarttraffic.smartparking.dataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class SmartParkingLot implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("radio")
    @Expose
    private float radio;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latitud_center")
    @Expose
    private float latitud_center;
    @SerializedName("longitud_center")
    @Expose
    private float longitud_center;
    @SerializedName("spots_in")
    @Expose
    private Integer spots_in;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("updated")
    @Expose
    private Date updated;

    @Override
    public String toString() {
        return "SmartParkingLot{" +
                "id=" + id +
                ", radio=" + radio +
                ", name='" + name + '\'' +
                ", latitud_center=" + latitud_center +
                ", longitud_center=" + longitud_center +
                ", spots_in=" + spots_in +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public float getLatitud_center() {
        return latitud_center;
    }

    public void setLatitud_center(float latitud_center) {
        this.latitud_center = latitud_center;
    }

    public float getLongitud_center() {
        return longitud_center;
    }

    public void setLongitud_center(float longitud_center) {
        this.longitud_center = longitud_center;
    }

    public Integer getSpots_in() {
        return spots_in;
    }

    public void setSpots_in(Integer spots_in) {
        this.spots_in = spots_in;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
