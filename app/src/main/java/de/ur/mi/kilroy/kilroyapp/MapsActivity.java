package de.ur.mi.kilroy.kilroyapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

import de.ur.mi.kilroy.kilroyapp.helper.LocationUpdater;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

public class MapsActivity extends FragmentActivity implements LocationUpdater.locationUpdateListener {

    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static LocationUpdater locationUpdater;
    private IntentFilter[] intentFiltersArray;

    public static LocationUpdater getLocationUpdater() {
        return getLocationUpdater();
    }

    private HashMap<Marker, MarkerItem> tagMarkerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        requestLocationUpdates();
        initMapCamera();
        Log.d("maps started");

//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, new Intent(this, KilroyNfcReaderActivity.class.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//
//        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
//                                       You should specify only the ones that you need. */
//        }
//        catch (IntentFilter.MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);
//        }
//        intentFiltersArray = new IntentFilter[] {ndef, };

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
        tagMarkerMap = new HashMap<>();

//        String url = "http://localhost:8080/api/posts";
        StringRequest request = new StringRequest(AppController.URL + "posts", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Collection<PostItem> postItems = gson.fromJson(response, type);

                for (MarkerItem item : postItems) {
                    Marker marker = placeMarker(item);
                    tagMarkerMap.put(marker, item);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError: ", error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(request);


    }
//    private void setupMarkers() {
//        DummyMarker dummys = new DummyMarker();
//        ArrayList<MarkerItem> dummyMarkers = dummys.getDummyMarkers();
//        tagMarkerMap = new HashMap<>();
//
//        for (int i = 0; i < dummyMarkers.size(); i++) {
//            MarkerItem markerItem = dummyMarkers.get(i);
//            Marker marker = placeMarker(markerItem);
//            tagMarkerMap.put(marker, markerItem);
//
//            if(tagMarkerMap.get(marker).equals(markerItem)){
//                Log.d("marker is palced and added to hashmap");
//            }else{
//                Log.d("marker is placed but not in hashmap");
//            }
//
//        }
//    }

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
            setupMarkers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_write_tag) {
            Intent intent = new Intent(MapsActivity.this, KilroyNfcTagWriterActivity.class);
            intent.putExtra("lat", locationUpdater.getLastKnownLocation().getLatitude());
            intent.putExtra("lng", locationUpdater.getLastKnownLocation().getLongitude());
            startActivityForResult(intent, AppController.NFC_TAG_WRITER_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppController.NFC_TAG_WRITER_REQUEST) {
            if (resultCode == KilroyNfcTagWriterActivity.NFC_TAG_WRITER_DONE) {
                // TODO: Any result here?
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateMap(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        mMap.animateCamera(cameraUpdate);
    }
}
