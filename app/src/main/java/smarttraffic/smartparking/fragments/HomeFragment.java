package smarttraffic.smartparking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

    @BindView(R.id.mapFragment)
    MapView mapView;
    @BindView(R.id.latitudOfUser)
    TextView latitudToShow;
    @BindView(R.id.longitudOfUser)
    TextView longitudToShow;
    @BindView(R.id.distanceTo)
    TextView distanceTo;

    BroadcastReceiver broadcastReceiver;
    Location gpsLocation = new Location(LocationManager.GPS_PROVIDER);
    Location Ucampus = new Location("dummyprovider");
    Location home = new Location("dummyprovider");
    Location sanRafael = new Location("dummyprovider");

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_layout, container, false);
        ButterKnife.bind(this, view);

        Ucampus.setLatitude(-25.325624);
        Ucampus.setLongitude(-57.637866);
        home.setLatitude(-25.306100);
        home.setLongitude(-57.591436);
        sanRafael.setLatitude(-25.307299);
        sanRafael.setLongitude(-57.587078);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.getBroadcastLocationIntent())) {
                    gpsLocation.setLatitude(intent.getDoubleExtra("latitud", 0));
                    gpsLocation.setLongitude(intent.getDoubleExtra("longitud", 0));
                    //Set text for view...
                    latitudToShow.setText(String.valueOf(gpsLocation.getLatitude()));
                    longitudToShow.setText(String.valueOf(gpsLocation.getLongitude()));
                    distanceTo.setText("To home: " + String.valueOf(gpsLocation.distanceTo(home)) + "\n"
                            + "To UCA: " + String.valueOf(gpsLocation.distanceTo(Ucampus)) + "\n"
                            + "To church: " + String.valueOf(gpsLocation.distanceTo(sanRafael)));
                }
            }
        };

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        mapView.setTileSource(new OnlineTileSourceBase("SMARTPARKING CartoDB", 10, 22,
                256, ".png",
                new String[] { "http://192.168.100.49:80/tile/" }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(-25.323740, -57.638405);
        mapController.setCenter(startPoint);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.getBroadcastLocationIntent()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

    }
}