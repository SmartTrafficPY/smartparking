package smarttraffic.smartparking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.dataModels.SmartParkingSpot;

/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */

public class HomeFragment extends Fragment {

    private static final String LOG_TAG = "HomeFragment";

    private MyLocationNewOverlay mLocationOverlay;
    private Marker userMarker;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    @BindView(R.id.mapFragment)
    MapView mapView;
    List<GeoPoint> polygonsSpots;
    Polygon polygon = new Polygon();
    BroadcastReceiver broadcastReceiver;
    BroadcastReceiver geofenceReceiver;

    Location gpsLocation = new Location(LocationManager.GPS_PROVIDER);

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_layout, container, false);
        ButterKnife.bind(this, view);
        //gets the location...
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.getBroadcastLocationIntent())) {
                    gpsLocation.setLatitude(intent.getDoubleExtra(Constants.getLatitud(), 0));
                    gpsLocation.setLongitude(intent.getDoubleExtra(Constants.getLongitud(), 0));
                }
            }
        };

        geofenceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.getBroadcastGeofenceTriggerIntent())) {
                    ArrayList<String> geofencesTriggers = intent.getStringArrayListExtra(Constants.GEOFENCE_TRIGGER_ID);
                    for (String geofenceId : geofencesTriggers) {
                        addParkingSpotsList(geofenceId);
                    }
                }
            }
        };

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
        IMapController mapController = mapView.getController();

        setGralMapConfiguration(mapController);

        //scale bar
        setScaleBar();

        setLocationOverlay();

        setCompassGestureOverlays();

        //add all overlays
        addOverlays();
    }

    private void geoListPopulated(List<GeoPoint> geoPoints) {
        geoPoints.add(new GeoPoint(-25.30601257,-57.5917075));
        geoPoints.add(new GeoPoint(-25.30604186,-57.59168641));
        geoPoints.add(new GeoPoint(-25.30609232,-57.5917231));
        geoPoints.add(new GeoPoint(-25.30607715,-57.59174553));
        geoPoints.add(new GeoPoint(-25.30601257,-57.5917075));
    }

    private void addOverlays(){
        mapView.getOverlays().add(mRotationGestureOverlay);
        mapView.getOverlays().add(mCompassOverlay);
        mapView.getOverlays().add(mLocationOverlay);
        mapView.getOverlays().add(mScaleBarOverlay);
//        mapView.getOverlays().add(userMarker);
//        mapView.getOverlayManager().add(polygon);
    }

    private void setScaleBar(){
        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
    }

    private void userMarker(IMapController mapController){
        userMarker = new Marker(mapView);
        userMarker.setDefaultIcon();
        userMarker.setIcon(getResources().getDrawable(R.drawable.marker_icon));
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setPosition(new GeoPoint(gpsLocation.getLatitude(),gpsLocation.getLongitude()));
        mapController.setCenter(new GeoPoint(gpsLocation.getLatitude(),gpsLocation.getLongitude()));
    }

    private void setLocationOverlay(){
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
    }

    private void setCompassGestureOverlays(){
        //add compass
        mCompassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), mapView);
        mCompassOverlay.enableCompass();

        //rotation gestures
        mRotationGestureOverlay = new RotationGestureOverlay(getActivity(), mapView);
        mRotationGestureOverlay.setEnabled(true);
    }

    private void setGralMapConfiguration(IMapController mapController){
        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setFlingEnabled(true);
        mapController.setZoom(17);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.getBroadcastLocationIntent()));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(geofenceReceiver,
                new IntentFilter(Constants.getBroadcastGeofenceTriggerIntent()));
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.enableMyLocation();
        mScaleBarOverlay.disableScaleBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(geofenceReceiver);
        mCompassOverlay.disableCompass();
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
        mScaleBarOverlay.enableScaleBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocationOverlay=null;
        mCompassOverlay=null;
        mScaleBarOverlay=null;
        mRotationGestureOverlay=null;

    }

    private void addParkingSpotsList(String geofenceTriggerId) {
        polygonsSpots = new ArrayList<>();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://192.168.100.5:8000/smartparking/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<List<SmartParkingSpot>> call = smartParkingAPI.getAllSpotsInLot(geofenceTriggerId);

        call.enqueue(new Callback<List<SmartParkingSpot>>() {
            @Override
            public void onResponse(Call<List<SmartParkingSpot>> call, Response<List<SmartParkingSpot>> response) {
                switch (response.code()) {
                    case 200:
                        for(SmartParkingSpot spot : response.body()){
                            drawPolygon(spotToListOfGeoPoints(spot));
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<SmartParkingSpot>> call, Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG,t.toString());
            }
        });
    }

    private List<GeoPoint> spotToListOfGeoPoints(SmartParkingSpot spot){
        List<GeoPoint> polygon = new ArrayList<>();
        polygon.add(new GeoPoint(spot.getP1_latitud(),spot.getP1_longitud()));
        polygon.add(new GeoPoint(spot.getP2_latitud(),spot.getP2_longitud()));
        polygon.add(new GeoPoint(spot.getP3_latitud(),spot.getP3_longitud()));
        polygon.add(new GeoPoint(spot.getP4_latitud(),spot.getP4_longitud()));
        polygon.add(new GeoPoint(spot.getP5_latitud(),spot.getP5_longitud()));
        return polygon;
    }

    private void drawPolygon(List<GeoPoint> geoPoints){
        Polygon polygon = new Polygon();    //see note below
        polygon.setFillColor(Color.parseColor("#FF0000"));
        polygon.setStrokeColor(Color.parseColor("#00FF00"));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);
        mapView.getOverlayManager().add(polygon);
//        polygon.setTitle("A sample polygon");
    }
}