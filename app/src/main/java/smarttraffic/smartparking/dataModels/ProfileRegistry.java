package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class ProfileRegistry {

    private String password;
    private String username;
    private SmartParkingProfile smartparkingprofile;

    @Override
    public String toString() {
        return "ProfileRegistry{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", smartparkingprofile=" + smartparkingprofile +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SmartParkingProfile getSmartParkingProfile() {
        return smartparkingprofile;
    }

    public void setSmartParkingProfile(SmartParkingProfile smartParkingProfile) {
        this.smartparkingprofile = smartParkingProfile;
    }

}

