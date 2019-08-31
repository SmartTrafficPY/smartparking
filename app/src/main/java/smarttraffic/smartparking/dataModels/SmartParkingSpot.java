package smarttraffic.smartparking.dataModels;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */
public class SmartParkingSpot implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("in_lot")
    @Expose
    private Integer in_lot;
    @SerializedName("p1_latitud")
    @Expose
    private double p1_latitud;
    @SerializedName("p1_longitud")
    @Expose
    private double p1_longitud;
    @SerializedName("p2_latitud")
    @Expose
    private double p2_latitud;
    @SerializedName("p2_longitud")
    @Expose
    private double p2_longitud;
    @SerializedName("p3_latitud")
    @Expose
    private double p3_latitud;
    @SerializedName("p3_longitud")
    @Expose
    private double p3_longitud;
    @SerializedName("p4_latitud")
    @Expose
    private double p4_latitud;
    @SerializedName("p4_longitud")
    @Expose
    private double p4_longitud;
    @SerializedName("p5_latitud")
    @Expose
    private double p5_latitud;
    @SerializedName("p5_longitud")
    @Expose
    private double p5_longitud;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("updated")
    @Expose
    private Date updated;

    @Override
    public String toString() {
        return "SmartParkingSpot{" +
                "id=" + id +
                ", in_lot=" + in_lot +
                ", p1_latitud=" + p1_latitud +
                ", p1_longitud=" + p1_longitud +
                ", p2_latitud=" + p2_latitud +
                ", p2_longitud=" + p2_longitud +
                ", p3_latitud=" + p3_latitud +
                ", p3_longitud=" + p3_longitud +
                ", p4_latitud=" + p4_latitud +
                ", p4_longitud=" + p4_longitud +
                ", p5_latitud=" + p5_latitud +
                ", p5_longitud=" + p5_longitud +
                ", status='" + status + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    public List<LatLng> toLatLngList(){
        List<LatLng> resultList = new ArrayList<LatLng>() ;
        resultList.add(new LatLng(p1_latitud,p1_longitud));
        resultList.add(new LatLng(p2_latitud,p2_longitud));
        resultList.add(new LatLng(p3_latitud,p3_longitud));
        resultList.add(new LatLng(p4_latitud,p4_longitud));
        resultList.add(new LatLng(p5_latitud,p5_longitud));
        return resultList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIn_lot() {
        return in_lot;
    }

    public void setIn_lot(Integer in_lot) {
        this.in_lot = in_lot;
    }

    public double getP1_latitud() {
        return p1_latitud;
    }

    public void setP1_latitud(double p1_latitud) {
        this.p1_latitud = p1_latitud;
    }

    public double getP1_longitud() {
        return p1_longitud;
    }

    public void setP1_longitud(double p1_longitud) {
        this.p1_longitud = p1_longitud;
    }

    public double getP2_latitud() {
        return p2_latitud;
    }

    public void setP2_latitud(double p2_latitud) {
        this.p2_latitud = p2_latitud;
    }

    public double getP2_longitud() {
        return p2_longitud;
    }

    public void setP2_longitud(double p2_longitud) {
        this.p2_longitud = p2_longitud;
    }

    public double getP3_latitud() {
        return p3_latitud;
    }

    public void setP3_latitud(double p3_latitud) {
        this.p3_latitud = p3_latitud;
    }

    public double getP3_longitud() {
        return p3_longitud;
    }

    public void setP3_longitud(double p3_longitud) {
        this.p3_longitud = p3_longitud;
    }

    public double getP4_latitud() {
        return p4_latitud;
    }

    public void setP4_latitud(double p4_latitud) {
        this.p4_latitud = p4_latitud;
    }

    public double getP4_longitud() {
        return p4_longitud;
    }

    public void setP4_longitud(double p4_longitud) {
        this.p4_longitud = p4_longitud;
    }

    public double getP5_latitud() {
        return p5_latitud;
    }

    public void setP5_latitud(double p5_latitud) {
        this.p5_latitud = p5_latitud;
    }

    public double getP5_longitud() {
        return p5_longitud;
    }

    public void setP5_longitud(double p5_longitud) {
        this.p5_longitud = p5_longitud;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
