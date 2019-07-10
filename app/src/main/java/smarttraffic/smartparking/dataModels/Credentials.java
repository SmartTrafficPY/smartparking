package smarttraffic.smartparking.dataModels;

import java.io.Serializable;

public class Credentials implements Serializable {

    private String alias;
    private String password;

    public Credentials() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "alias='" + alias + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
