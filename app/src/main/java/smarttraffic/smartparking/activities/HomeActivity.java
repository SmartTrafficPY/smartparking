package smarttraffic.smartparking.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.MyLocationListener;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.fragments.AboutFragment;
import smarttraffic.smartparking.fragments.ChangePassFragment;
import smarttraffic.smartparking.fragments.HomeFragment;
import smarttraffic.smartparking.fragments.LogOutFragment;
import smarttraffic.smartparking.fragments.SettingsFragment;
import smarttraffic.smartparking.receivers.ProximityAlert;

/**
 * Created by Joaquin Olivera on july 19.
 *
 * @author joaquin
 */

public class HomeActivity extends AppCompatActivity {

    private static final long POINT_RADIUS = 50;
    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.navview)
    NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<Location> parkingLots;

    Location Ucampus = new Location("dummyprovider");
    Location home = new Location("dummyprovider");
    Location sanRafael = new Location("dummyprovider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);

        addParkingLots();

        Intent intent = getIntent();
        String intentStarter = intent.getStringExtra(Constants.getIntentFrom());
        if(intentStarter == Constants.getFromProximityIntent()){
            //started from proximity alert ...
        }else{
            registerParkingAlerts();
            startGpsUpdateRequest();
        }

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
    protected void onResume() {
        super.onResume();
        startGpsUpdateRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(proximityAlert);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
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

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
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
        Ucampus.setLatitude(-25.325624);
        Ucampus.setLongitude(-57.637866);
        home.setLatitude(-25.306100);
        home.setLongitude(-57.591436);
        sanRafael.setLatitude(-25.307299);
        sanRafael.setLongitude(-57.587078);
        parkingLots.add(Ucampus);
//        parkingLots.add(home);
        parkingLots.add(sanRafael);
    }

    private void registerParkingAlerts() {
        for(int i = 0; i < parkingLots.size(); i++) {
            Location location = parkingLots.get(i);
            setProximityAlert(location.getLatitude(),
                    location.getLongitude(),
                    POINT_RADIUS,
                    i+1, //NExt could be the parkingLot ID...
                    i);
        }
    }

    private void setProximityAlert(double lat, double lon, long radius, final int eventID, int requestCode) {
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

        IntentFilter filter = new IntentFilter(Constants.getProximityIntentAction());
        ProximityAlert proximityAlert = new ProximityAlert();
        registerReceiver(proximityAlert, filter);
    }

    private void startGpsUpdateRequest() {

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Precaucion");
            alertDialog.setMessage("Favor habilite el GPS desde las configuraciones");
            alertDialog.setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        } else {
            Log.v(LOG_TAG, "GPS Enabled");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    Constants.getLowFrequencyUpdates(),
                    Constants.getDistanceChangeForUpdates(), new MyLocationListener(this));
        }
    }


}
