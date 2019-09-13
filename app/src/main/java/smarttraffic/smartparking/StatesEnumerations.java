package smarttraffic.smartparking;

/**
 * Created by Joaquin on 08/2019.
 * <p>
 * smarttraffic.smartparking
 */

public enum StatesEnumerations {
    FREE("F"),
    UNKNOWN("U"),
    OCCUPIED("O");

    private String estado;

    public String getEstado() {
        return estado;
    }

    StatesEnumerations(String f) {
        this.estado = f;
    }
}

