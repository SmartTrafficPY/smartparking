package smarttraffic.smartparking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import smarttraffic.smartparking.activities.HomeActivity;
import smarttraffic.smartparking.services.LoginService;

public class LoginReceiver extends BroadcastReceiver {

    private static final String TAG = "LoginReceiver";
    private String sex;
    private Integer age;
    private Integer identifier;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String errorMessage;

    public String getSexResponse() {
        return sex;
    }

    public void setSexResponse(String sexResponse) {
        this.sex = sexResponse;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(LoginService.LOGIN_ACTION)) {
            Bundle extras = intent.getExtras();
            setAge(extras.getInt("age", -1));
            setIdentifier(extras.getInt("identifier", 0));
            setSexResponse(extras.getString("sex"));
            if (identifier != 0){
                Intent i = new Intent(context, HomeActivity.class);
                i.putExtra("id", getIdentifier());
                context.startActivity(i);
            }
        }
        else if(intent.getAction().equals(LoginService.BAD_LOGIN_ACTION)) {
            setErrorMessage(intent.getStringExtra("not_exists"));
            Toast.makeText(context, "Wrong Alias or Password!", Toast.LENGTH_LONG).show();
        }
    }
}
