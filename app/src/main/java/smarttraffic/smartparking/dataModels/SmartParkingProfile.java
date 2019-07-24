package smarttraffic.smartparking.dataModels;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class SmartParkingProfile {

    private String birth_date;
    private String sex;

    public SmartParkingProfile() {
    }

    @Override
    public String toString() {
        return "SmartParkingProfile{" +
                "birth_date='" + birth_date + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public String getBirth_date() {
            return birth_date;
    }

    public void setBirth_date(String birth_date) {
            this.birth_date = birth_date;
    }

    public String getSex() {
            return sex;
    }

    public void setSex(String sex) {
            this.sex = sex;
        }

}
