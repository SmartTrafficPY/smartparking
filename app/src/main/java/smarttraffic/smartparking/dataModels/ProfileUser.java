package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class ProfileUser {

    private String url;
    private String username;
    private SmartParkingProfile smartparkingprofile;

    public ProfileUser() {
        // Persistence Constructor
    }

    @Override
    public String toString() {
        return "ProfileUser{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", smartParkingProfile=" + smartparkingprofile +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
