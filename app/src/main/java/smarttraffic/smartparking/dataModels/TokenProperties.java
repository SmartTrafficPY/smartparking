package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 10/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */
public class TokenProperties {

    public TokenProperties(String app_token) {
        this.app_token = app_token;
    }

    private String app_token;

    public String getApp_token() {
        return app_token;
    }

    public void setApp_token(String app_token) {
        this.app_token = app_token;
    }
}
