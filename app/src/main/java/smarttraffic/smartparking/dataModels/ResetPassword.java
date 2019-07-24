package smarttraffic.smartparking.dataModels;

public class ResetPassword {
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "ResetPassword{" +
                "username='" + username + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public ResetPassword(String username, String birth_date, String sex) {
        this.username = username;
        this.birth_date = birth_date;
        this.sex = sex;
    }

    public void setUsername(String username) {
        this.username = username;
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

    private String username;
    private String birth_date;
    private String sex;

}
