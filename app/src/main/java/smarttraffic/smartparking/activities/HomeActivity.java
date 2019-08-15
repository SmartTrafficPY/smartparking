package smarttraffic.smartparking.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.fragments.AboutFragment;
import smarttraffic.smartparking.fragments.ChangePassFragment;
import smarttraffic.smartparking.fragments.HomeFragment;
import smarttraffic.smartparking.fragments.LogOutFragment;
import smarttraffic.smartparking.fragments.SettingsFragment;
import smarttraffic.smartparking.receivers.ProximityAlert;

import static smarttraffic.smartparking.R.mipmap.smartparking_logo_round;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class HomeActivity extends AppCompatActivity {

    /**
     * IF the user is log in, can enter here...else: Login first
     * HERE should show the map with the info of parking spots status
     * Father of:
     * -About
     * -Change Pass
     * -Settings
     * */

    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.navview)
    NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;

    private ArrayList<Location> parkingLots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);

//        sendNotification(1, "SmartParking", "This is just a test to see the Notification");
        
//        Add all the parking zones...
        addParkingLots();
        registerParkingAlerts();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment initialFragment = null;
        try {
            initialFragment = (Fragment) HomeFragment.class.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, initialFragment).commit();
        /**
         * Here we are dealing with the navigationMenu
         * **/
        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.home_menu:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.menu_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.menu_changepass:
                fragmentClass = ChangePassFragment.class;
                break;
            case R.id.menu_logout:
                fragmentClass = LogOutFragment.class;
                break;
            case R.id.menu_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Show images in Toast prompt.
    @SuppressLint("ResourceAsColor")
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.toast_smartparking);
        toastContentView.addView(imageView, 0);
        toast.show();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void addParkingLots() {
        //After we will need to look for the server to get the info of all the Lots...
        parkingLots = new ArrayList<Location>();
        Location ucaCampus = new Location("dummyProvider");
        ucaCampus.setLatitude(-25.323740);
        ucaCampus.setLongitude(-57.638405);
        parkingLots.add(ucaCampus);
    }

    private void registerParkingAlerts() {
        for(int i = 0; i < parkingLots.size(); i++) {
            Location location = parkingLots.get(i);
            setProximityAlert(location.getLatitude(),
                    location.getLongitude(),
                    i+1,
                    i);
        }
    }

    private void setProximityAlert(double lat, double lon, final long eventID, int requestCode) {

        // 500 meter radius
        float radius = 500f;

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        Intent intent = new Intent(Constants.getProximityIntentAction());
        intent.putExtra(ProximityAlert.EVENT_ID_INTENT_EXTRA, eventID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addProximityAlert(lat, lon, radius, -1, pendingIntent);
    }

}
