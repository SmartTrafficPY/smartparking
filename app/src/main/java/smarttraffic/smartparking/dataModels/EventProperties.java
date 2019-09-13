package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.dataModels
 */

public class EventProperties {

    private String application;
    private String e_type;
    private String agent;

    public EventProperties() {
    }

    @Override
    public String toString() {
        return "EventProperties{" +
                "application=" + application +
                ", e_type='" + e_type + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getE_type() {
        return e_type;
    }

    public void setE_type(String e_type) {
        this.e_type = e_type;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
