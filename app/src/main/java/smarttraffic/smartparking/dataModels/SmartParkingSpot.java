package smarttraffic.smartparking.dataModels;

import java.util.Date;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */
public class SmartParkingSpot {

    private Integer id;
    private Integer in_lot;
    private double p1_latitud;
    private double p1_longitud;
    private double p2_latitud;
    private double p2_longitud;
    private double p3_latitud;
    private double p3_longitud;
    private double p4_latitud;
    private double p4_longitud;
    private double p5_latitud;
    private double p5_longitud;
    private String status;
    private Date created;
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
