package smarttraffic.smartparking.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.activities
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.saveSettings)
    Button saveSettings;
    //Location Ubication...
    @BindView(R.id.superFastTimeGPS)
    RadioButton superFastTimeGPS;
    @BindView(R.id.fastTimeGPS)
    RadioButton fastTimeGPS;
    @BindView(R.id.normalTimeGPS)
    RadioButton normalTimeGPS;
    @BindView(R.id.slowTimeGPS)
    RadioButton slowTimeGPS;
    @BindView(R.id.gpsActualizationsTime)
    RadioGroup gpsActualizationsTime;
    //Time updates on Maps...
    @BindView(R.id.fastUpdateRequest)
    RadioButton fastUpdateRequest;
    @BindView(R.id.normalUpdateRequest)
    RadioButton normalUpdateRequest;
    @BindView(R.id.slowUpdateRequest)
    RadioButton slowUpdateRequest;
    @BindView(R.id.requestActualizationsTime)
    RadioGroup requestActualizationsTime;

    @BindView(R.id.pointsDraw)
    RadioButton pointsDraw;
    @BindView(R.id.polygonDraw)
    RadioButton polygonDraw;
    @BindView(R.id.drawGroup)
    RadioGroup drawGroup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


//    public String onRadioButtonClicked() {
//        if(superFastTimeGPS.isChecked()){
//            return "F";
//        }else if(fastTimeGPS.isChecked()){
//            return "M";
//        }else{
//            return null;
//        }
//    }

}
