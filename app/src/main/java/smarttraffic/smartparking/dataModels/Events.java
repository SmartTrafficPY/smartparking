package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class Events {

    private int userId;
    private int lotId;
    private String action = "E";

    public Events() {
    }

    @Override
    public String toString() {
        return "Events{" +
                "userId=" + userId +
                ", lotId=" + lotId +
                ", action='" + action + '\'' +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
