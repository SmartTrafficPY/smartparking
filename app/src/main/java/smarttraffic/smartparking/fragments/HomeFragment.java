package smarttraffic.smartparking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import smarttraffic.smartparking.Constants;
import smarttraffic.smartparking.R;

/**
 * Created by Joaquin Olivera on august 19.
 *
 * @author joaquin
 */

public class HomeFragment extends Fragment {

    private MyLocationNewOverlay mLocationOverlay;
    private Marker userMarker;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    @BindView(R.id.mapFragment)
    MapView mapView;

    BroadcastReceiver broadcastReceiver;
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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.getBroadcastLocationIntent())) {
                    gpsLocation.setLatitude(intent.getDoubleExtra("latitud", 0));
                    gpsLocation.setLongitude(intent.getDoubleExtra("longitud", 0));
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

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);

//        userMarker = new Marker(mapView);
////        userMarker.setDefaultIcon();
//        userMarker.setIcon(getResources().getDrawable(R.drawable.marker_icon));
//        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        userMarker.setPosition(new GeoPoint(gpsLocation.getLatitude(),gpsLocation.getLongitude()));
//        mapController.setCenter(new GeoPoint(gpsLocation.getLatitude(),gpsLocation.getLongitude()));

        List<GeoPoint> geoPoints = new ArrayList<>();
        geoListPopulated(geoPoints);
        Polygon polygon = new Polygon();    //see note below
        polygon.setFillColor(Color.parseColor("#FF0000"));
        polygon.setStrokeColor(Color.parseColor("#00FF00"));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);
        polygon.setTitle("A sample polygon");

//polygons supports holes too, points should be in a counter-clockwise order
        List<List<GeoPoint>> holes = new ArrayList<>();
        holes.add(geoPoints);
        polygon.setHoles(holes);
//        mapView.getOverlayManager().add(polygon);


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
//        mapView.getOverlays().add(userMarker);
        mapView.getOverlays().add(polygon);

    }

    private void geoListPopulated(List<GeoPoint> geoPoints) {
        geoPoints.add(new GeoPoint(-25.30601257,-57.5917075));
        geoPoints.add(new GeoPoint(-25.30604186,-57.59168641));
        geoPoints.add(new GeoPoint(-25.30609232,-57.5917231));
        geoPoints.add(new GeoPoint(-25.30607715,-57.59174553));
        geoPoints.add(new GeoPoint(-25.30601257,-57.5917075));

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.getBroadcastLocationIntent()));
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.enableMyLocation();
        mScaleBarOverlay.disableScaleBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
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
}