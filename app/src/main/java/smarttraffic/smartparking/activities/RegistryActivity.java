package smarttraffic.smartparking.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.receivers.RegistrationReceiver;
import smarttraffic.smartparking.services.RegistrationService;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class RegistryActivity extends AppCompatActivity {

    /**
     * The user register and get a profile on the system...
     * **/

    private static final String LOG_TAG = "RegistryActivity";

    private static final String CERO = "0";
    private static final String GUION = "-";

    @BindView(R.id.usernameSignUp)
    TextView usernameInput;
    @BindView(R.id.passwordSignUp)
    EditText passwordInput;
    @BindView(R.id.birthDate)
    TextView birthDate;
    @BindView(R.id.maleRadButton)
    RadioButton maleRadButton;
    @BindView(R.id.femaleRadButton)
    RadioButton femaleRadButton;
    @BindView(R.id.datePickerButton)
    ImageButton datePickerButton;
    @BindView(R.id.sexRadioGroup)
    RadioGroup sexSelectRadioGroup;
    @BindView(R.id.acceptTermsCheckBox)
    CheckBox termsAndConditions;
    @BindView(R.id.textInTermsAndCond)
    TextView textInTermsAndCond;
    @BindView(R.id.signUpButton)
    Button signInButton;
    @BindView(R.id.goToLogin)
    Button goToLogin;
    @BindView(R.id.passModeButton)
    ImageButton passwordModeButton;
    @BindView(R.id.setRandomUser)
    ImageButton randomUser;

    public final Calendar calendar = Calendar.getInstance();
    private final int MAX_LENGTH = 10;
    public static Random RANDOM = new Random();
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    final int actuallMonth = calendar.get(Calendar.MONTH);
    final int actuallDay = calendar.get(Calendar.DAY_OF_MONTH);
    final int actuallYear = calendar.get(Calendar.YEAR);
    private boolean showPasswordText = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registry_layout);
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
        filter.addAction(RegistrationService.REGISTRATION_OK);
        filter.addAction(RegistrationService.BAD_REGISTRATION);
        RegistrationReceiver registrationReceiver = new RegistrationReceiver();
        registerReceiver(registrationReceiver, filter);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                getDatePickedUp();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRegister();
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        passwordModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                //TODO: if image is showText set TO don't ShowText
                // and set password text Visual for user...
                if(!showPasswordText){
                    passwordModeButton.setImageDrawable(
                            context.getDrawable(R.drawable.dontshowtext));
                    //Show Password:
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPasswordText = !showPasswordText;
                }else{
                    passwordModeButton.setImageDrawable(context.getDrawable(R.drawable.showtext));
                    //Hide Password:
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPasswordText = !showPasswordText;
                }
            }
        });
        usernameInput.setText(randomString());
        randomUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput.setText(randomString());
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(RegistryActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getDatePickedUp() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                birthDate.setText(year + GUION + mesFormateado + GUION + diaFormateado);
            }

        }, actuallYear, actuallMonth, actuallDay);
        datePickerDialog.show();
    }

    private void createRegister() {
        Log.d(LOG_TAG, "User trying to registry");

        signInButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistryActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando el registro...");

        if(dataIsCorrectlyComplete()){
            sendRegistrationPetition();
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            /**Here the service get the request of registration...**/
                            signInButton.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
    }

    private boolean dataIsCorrectlyComplete() {
        if(termsAndConditions.isChecked()){
            if(passwordInput.getText().toString().length() > 5){
                if(maleRadButton.isChecked() || femaleRadButton.isChecked()){
                    if(!birthDate.getText().toString().isEmpty()){
                        return true;
                    }else{
                        showToast("Favor ponga su EDAD(en años)!");
                        return false;
                    }
                }else{
                    showToast("Es necesario elegir alguna opcion de SEXO!");
                    return false;
                }
            }else{
                showToast("La CONTRASEÑA debe tener al menos 6 caracteres!");
                return false;
            }
        }else{
            showToast("Tienes que aceptar los TERMINOS y CONDICIONES!");
            return false;
        }
    }

    private void sendRegistrationPetition() {
        Intent registryIntent = new Intent(RegistryActivity.this, RegistrationService.class);
        registryIntent.putExtra("username", usernameInput.getText().toString());
        registryIntent.putExtra("password", passwordInput.getText().toString());
        registryIntent.putExtra("birth_date", birthDate.getText().toString());
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

    // Show images in Toast prompt.
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.smartparking_logo_round);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    private String randomString() {
        StringBuilder sb = new StringBuilder(MAX_LENGTH);

        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        return sb.toString();
    }

}
