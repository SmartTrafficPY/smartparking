package smarttraffic.smartparking.dataModels;

public class ProfileRegistry {

    private String password;
    private String alias;
    private Integer age;
    private String sex;

    @Override
    public String toString() {
        return "ProfileRegistry{" +
                "password='" + password + '\'' +
                ", alias='" + alias + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


}
