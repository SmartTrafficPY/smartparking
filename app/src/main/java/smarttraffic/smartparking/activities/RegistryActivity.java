package smarttraffic.smartparking.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.RegistrationReceiver;
import smarttraffic.smartparking.services.RegistrationService;

public class RegistryActivity extends AppCompatActivity {

    /**
     * The user register and get a profile on the system...
     * **/

    private static final String TAG = "RegistryActivity";

    @BindView(R.id.aliasSignUp)
    EditText aliasInput;
    @BindView(R.id.passwordSignUp)
    EditText passwordInput;
    @BindView(R.id.ageSignUp)
    EditText ageInput;
    @BindView(R.id.maleRadButton)
    RadioButton maleRadButton;
    @BindView(R.id.femaleRadButton)
    RadioButton femaleRadButton;
    @BindView(R.id.sexRadioGroup)
    RadioGroup sexSelectRadioGroup;
    @BindView(R.id.acceptTermsCheckBox)
    CheckBox termsAndConditions;
    @BindView(R.id.textInTermsAndCond)
    TextView textInTermsAndCond;
    @BindView(R.id.signUpButton)
    Button signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registry_activity);
        ButterKnife.bind(this);

//        signInButton.setEnabled(false);
            //TODO:make EULAActivity...
//        textInTermsAndCond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegistryActivity.this, EulaActivity.class);
//                startActivity(intent);
//            }
//        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(RegistrationService.REGISTRATION_ACTION);
        filter.addAction(RegistrationService.BAD_REGISTRATION_ACTION);
        RegistrationReceiver registrationReceiver = new RegistrationReceiver();
        registerReceiver(registrationReceiver, filter);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataIsComplete()){
                    sendRegistrationPetition();
                }
            }
        });

    }

    private boolean dataIsComplete() {
        if(termsAndConditions.isChecked()){
            if(!aliasInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()){
                if(maleRadButton.isChecked() || femaleRadButton.isChecked()){
                    if(ageInput.getText()!= null){
                        return true;
                    }else{
                        Toast.makeText(RegistryActivity.this,
                                "Favor ponga su edad(en años)!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else{
                    Toast.makeText(RegistryActivity.this,
                            "Es necesario elegir alguna opcion de sexo!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }else{
                Toast.makeText(RegistryActivity.this,
                        "Tanto el alias como el password son necesarios!", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(RegistryActivity.this,
                    "Tienes que aceptar los terminos y condiciones!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void sendRegistrationPetition() {
        Intent registryIntent = new Intent(RegistryActivity.this, RegistrationService.class);
        registryIntent.putExtra("alias", aliasInput.getText().toString());
        registryIntent.putExtra("password", passwordInput.getText().toString());
        registryIntent.putExtra("age", Integer.parseInt(ageInput.getText().toString()));
        if(onRadioButtonClicked() != null){
            registryIntent.putExtra("sex", onRadioButtonClicked());
        }
        startService(registryIntent);
    }

    public String onRadioButtonClicked() {
        if(femaleRadButton.isChecked()){
            return "F";
        }else if(maleRadButton.isChecked()){
            return "M";
        }else{
            return null;
        }
    }

}
