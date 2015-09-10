package de.ur.mi.kilroy.kilroyapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import de.ur.mi.kilroy.kilroyapp.helper.DummyMarker;
import de.ur.mi.kilroy.kilroyapp.helper.LocationUpdater;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;

public class MapsActivity extends FragmentActivity implements LocationUpdater.locationUpdateListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters
    private LocationUpdater locationUpdater;
    private HashMap<Marker, MarkerItem> tagMarkerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        requestLocationUpdates();
        initMapCamera();
        Log.d("maps started");

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            Log.d("map successfully created");
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        setupMarkers();
        setMapOnInfoWindowListener();
    }


    private void setupMarkers() {
        DummyMarker dummys = new DummyMarker();
        ArrayList<MarkerItem> dummyMarkers = dummys.getDummyMarkers();
        tagMarkerMap = new HashMap<>();

        for (int i = 0; i < dummyMarkers.size(); i++) {
            MarkerItem markerItem = dummyMarkers.get(i);
            Marker marker = placeMarker(markerItem);
            tagMarkerMap.put(marker, markerItem);

            if(tagMarkerMap.get(marker).equals(markerItem)){
                Log.d("marker is palced and added to hashmap");
            }else{
                Log.d("marker is placed but not in hashmap");
            }

        }
    }

    private Marker placeMarker(MarkerItem markerItem) {
        Marker m = mMap.addMarker(new MarkerOptions()
                .position(markerItem.getMarkerLocation())
                .title(markerItem.getName()));

        return m;
    }

    private void setMapOnInfoWindowListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(MapsActivity.this, MarkerDetailActivity.class);
                MarkerItem markerItem = tagMarkerMap.get(marker);
                Log.d("content of markerName after getting it from the hasmap: " + markerItem.getName());
                i.putExtra("markerItemName", markerItem.getName());
                i.putExtra("markerItemDescription", markerItem.getDescription());
                startActivity(i);
            }
        });
    }

    private void requestLocationUpdates() {
        locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();

    }

    // sets the initial position of the camera when activity is opened.
    private void initMapCamera() {
        Location location = locationUpdater.getLastKnownLocation();
        Log.d("is location null?" + location.toString());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13);
        mMap.animateCamera(update);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    //updates the map when the user moves a certain distance or after a certain time.
    @Override
    public void onLocationUpdateReceived(Location location) {
        if (location != null) {
            updateMap(location);
        }
    }

    private void updateMap(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        mMap.animateCamera(cameraUpdate);
    }
}
