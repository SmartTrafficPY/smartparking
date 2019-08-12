package smarttraffic.smartparking.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;

public class HomeFragment extends Fragment implements LocationListener {

    private MyLocationNewOverlay mLocationOverlay;
    private Marker userMarker;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private LocationManager locationManager;
    private Location currentLocation = null;
    private GeoPoint userLocation = new GeoPoint(-25.323740, -57.638405);

    @BindView(R.id.mapFragment)
    MapView mapView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_layout, container, false);
        ButterKnife.bind(this, view);

        /**
         * For now show the map provide by our tile server...
         * **/

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

        mapView.setTileSource(new OnlineTileSourceBase("SMARTPARKING CartoDB", 10, 22,
                256, ".png",
                new String[]{Constants.getTileServerUrl()}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * MAP configurations and overlays...
         * **/

        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setFlingEnabled(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);

        //scale bar
        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        //add user position
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
        //marker in his position...
        userMarker = new Marker(mapView);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setPosition(userLocation);
        mapController.setCenter(userLocation);

        //add compass
        mCompassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), mapView);
        mCompassOverlay.enableCompass();

        //rotation gestures
        mRotationGestureOverlay = new RotationGestureOverlay(getActivity(), mapView);
        mRotationGestureOverlay.setEnabled(true);

        //add all overlays
        mapView.getOverlays().add(mRotationGestureOverlay);
        mapView.getOverlays().add(mCompassOverlay);
        mapView.getOverlays().add(mLocationOverlay);
        mapView.getOverlays().add(mScaleBarOverlay);
        mapView.getOverlays().add(userMarker);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        }catch (Exception ex){}

        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0l,0f,this);
        }catch (Exception ex){}

        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.enableMyLocation();
        mScaleBarOverlay.disableScaleBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        try{
            locationManager.removeUpdates(this);
        }catch (Exception ex){}

        mCompassOverlay.disableCompass();
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
        mScaleBarOverlay.enableScaleBar();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;
        userLocation.setAltitude(location.getAltitude());
        userLocation.setLatitude(location.getLatitude());
        userLocation.setLongitude(location.getLongitude());
        userMarker.setPosition(userLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager=null;
        currentLocation=null;
        userLocation = null;
        mLocationOverlay=null;
        mCompassOverlay=null;
        mScaleBarOverlay=null;
        mRotationGestureOverlay=null;

    }

}