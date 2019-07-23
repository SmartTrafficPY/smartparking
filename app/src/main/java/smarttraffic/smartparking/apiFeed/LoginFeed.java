package smarttraffic.smartparking.apiFeed;

import java.sql.Timestamp;

public class LoginFeed {

    private Integer id;
    private String password;
    private Timestamp last_login;
    private String username;
    private String email;
    private Timestamp date_joined;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(Timestamp date_joined) {
        this.date_joined = date_joined;
    }

    @Override
    public String toString() {
        return "LoginFeed{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", last_login=" + last_login +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", date_joined=" + date_joined +
                '}';
    }

}

