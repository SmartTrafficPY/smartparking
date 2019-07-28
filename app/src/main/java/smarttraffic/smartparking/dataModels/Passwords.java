package smarttraffic.smartparking.dataModels;

public class Passwords {

    private String current_pass;
    private String new_pass;

    public Passwords() {
    }

    @Override
    public String toString() {
        return "Passwords{" +
                "current_pass='" + current_pass + '\'' +
                ", new_pass='" + new_pass + '\'' +
                '}';
    }

    public String getCurrent_pass() {
        return current_pass;
    }

    public void setCurrent_pass(String current_pass) {
        this.current_pass = current_pass;
    }

    public String getNew_pass() {
        return new_pass;
    }

    public void setNew_pass(String new_pass) {
        this.new_pass = new_pass;
    }
}
