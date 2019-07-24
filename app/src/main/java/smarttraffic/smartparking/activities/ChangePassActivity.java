package smarttraffic.smartparking.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.R;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class ChangePassActivity extends AppCompatActivity {

    /**
        The user can change his password...
     * */

    private static final String LOG_TAG = "ChangePassActivity";

    @BindView(R.id.changePasswordButton)
    Button changePassButton;
    @BindView(R.id.currentPassword)
    EditText currentPass;
    @BindView(R.id.newPassword1)
    EditText newPass1;
    @BindView(R.id.newPassword2)
    EditText newPass2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
        ButterKnife.bind(this);

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: send update request for user petition...
                // (id should be in SharedPreferences already)
                Log.v(LOG_TAG, "User trying to change his password");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
