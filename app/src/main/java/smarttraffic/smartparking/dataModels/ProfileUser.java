package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class ProfileUser {

    private Integer id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private SmartParkingProfile smartParkingProfile;

    public ProfileUser() {
    }

    @Override
    public String toString() {
        return "ProfileUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", smartParkingProfile=" + smartParkingProfile +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SmartParkingProfile getSmartParkingProfile() {
        return smartParkingProfile;
    }

    public void setSmartParkingProfile(SmartParkingProfile smartParkingProfile) {
        this.smartParkingProfile = smartParkingProfile;
    }
}
