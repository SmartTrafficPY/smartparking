package smarttraffic.smartparking.apiFeed;

public class LoginFeed {

    //Here we can get the token, or session id for later use...
    //... but for now we are just gonna receive the id of the user that correspond

    private String identifier;

    @Override
    public String toString() {
        return "LoginFeed{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

