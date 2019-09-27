package smarttraffic.smartparking.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.Utils;

/**
 * Created by Joaquin on 09/2019.
 * <p>
 * smarttraffic.smartparking.activities
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.saveSettings)
    Button saveSettings;
    //Location Ubication...
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

        SharedPreferences sharedPreferences = this.getSharedPreferences(
                Constants.SETTINGS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        
        checkValuesFromSettingsOptions(setSettingInfo(sharedPreferences));

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValuesFromSettingsOptions(saveAllNewSettings(editor));
                Utils.showToast(SettingsActivity.this.getResources().getString(R.string.new_configurations_accepted),
                        SettingsActivity.this);
                Utils.settingsHasChanged(SettingsActivity.this);
                finish();
            }
        });

    }

    private HashMap<String, String> saveAllNewSettings(SharedPreferences.Editor editor) {
        editor.putLong(Constants.LOCATION_TIME_UPDATE_SETTINGS, onRadioLocationClicked()).apply();
        editor.putLong(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS, onRadioMapUpdateClicked()).apply();
        editor.putString(Constants.DRAW_SETTINGS,
                String.valueOf(onRadioDrawClicked())).apply();
        editor.commit();
        return returnNewSettings();
    }

    private HashMap<String, String> returnNewSettings() {
        HashMap<String,String> newSettings = new HashMap<>();
        newSettings.put(Constants.LOCATION_TIME_UPDATE_SETTINGS, String.valueOf(onRadioLocationClicked()));
        newSettings.put(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS, String.valueOf(onRadioMapUpdateClicked()));
        newSettings.put(Constants.DRAW_SETTINGS, String.valueOf(onRadioDrawClicked()));
        return  newSettings;
    }

    private void checkValuesFromSettingsOptions(HashMap<String, String> settings) {
        checkForLocationUpdate(settings);
        checkForMapsUpdate(settings);
        checkForDrawOption(settings);
    }

    private HashMap<String, String> setSettingInfo(SharedPreferences sharedPreferences) {
        HashMap<String,String> options = new HashMap<>();
        options.put(Constants.LOCATION_TIME_UPDATE_SETTINGS, String.valueOf(sharedPreferences.getLong(
                Constants.LOCATION_TIME_UPDATE_SETTINGS, Constants.getSecondsInMilliseconds() * 5)));
        options.put(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS, String.valueOf(sharedPreferences.getLong(
                Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS,
                Constants.getSecondsInMilliseconds() * 45)));
        options.put(Constants.DRAW_SETTINGS,sharedPreferences.getString(
                Constants.DRAW_SETTINGS, Constants.POLYGON_TO_DRAW_SETTINGS));
        return options;
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

    public Long onRadioLocationClicked() {
        if(fastTimeGPS.isChecked()){
            return Constants.getSecondsInMilliseconds();
        }else if(slowTimeGPS.isChecked()){
            return Constants.getSecondsInMilliseconds() * 10;
        }else{
            return Constants.getSecondsInMilliseconds() * 5;
        }
    }

    public Long onRadioMapUpdateClicked() {
        if(fastUpdateRequest.isChecked()){
            return Constants.getSecondsInMilliseconds() * 30;
        }else if(slowUpdateRequest.isChecked()){
            return Constants.getMinutesInMilliseconds();
        }else{
            return Constants.getSecondsInMilliseconds() * 45;
        }
    }

    public String onRadioDrawClicked() {
        if(pointsDraw.isChecked()){
            return Constants.POINT_TO_DRAW_SETTINGS;
        }else{
            return Constants.POLYGON_TO_DRAW_SETTINGS;
        }
    }

    private void checkForLocationUpdate(HashMap<String, String> settings){
        if(settings.get(Constants.LOCATION_TIME_UPDATE_SETTINGS) != null){
            if(Long.valueOf(settings.get(Constants.LOCATION_TIME_UPDATE_SETTINGS)) ==
                    Constants.getSecondsInMilliseconds()){
                gpsActualizationsTime.check(R.id.fastTimeGPS);

            }else if(Long.valueOf(settings.get(Constants.LOCATION_TIME_UPDATE_SETTINGS))==
                    Constants.getSecondsInMilliseconds() * 5){
                gpsActualizationsTime.check(R.id.normalTimeGPS);

            }else{
                gpsActualizationsTime.check(R.id.slowTimeGPS);

            }
        }

    }

    private void checkForMapsUpdate(HashMap<String, String> settings){
        if(settings.get(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS) != null){
            if(Long.valueOf(settings.get(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS)) ==
                    Constants.getSecondsInMilliseconds() * 30){
                requestActualizationsTime.check(R.id.fastUpdateRequest);
            }else if(Long.valueOf(settings.get(Constants.MAP_SPOTS_TIME_UPDATE_SETTINGS)) ==
                    Constants.getSecondsInMilliseconds() * 45){
                requestActualizationsTime.check(R.id.normalUpdateRequest);
            }else{
                requestActualizationsTime.check(R.id.slowUpdateRequest);
            }
        }
    }

    private void checkForDrawOption(HashMap<String, String> settings){
        if(settings.get(Constants.DRAW_SETTINGS) != null){
            if(settings.get(Constants.DRAW_SETTINGS).equals(
                    Constants.POLYGON_TO_DRAW_SETTINGS)){
                drawGroup.check(R.id.polygonDraw);
            }else{
                drawGroup.check(R.id.pointsDraw);
            }
        }
    }

}
