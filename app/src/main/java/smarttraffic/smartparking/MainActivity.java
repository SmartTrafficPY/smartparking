package smarttraffic.smartparking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

//    Here should be asking if the user is already log in...
//    if it is, could just go to the central Activity or else
//    go to the LoginActivity...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
