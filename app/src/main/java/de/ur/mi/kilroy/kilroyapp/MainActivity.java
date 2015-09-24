package de.ur.mi.kilroy.kilroyapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.util.activity.NfcReaderActivity;
import org.ndeftools.wellknown.TextRecord;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

import de.ur.mi.kilroy.kilroyapp.helper.LocationUpdater;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;


public class MainActivity extends NfcReaderActivity implements OnMapReadyCallback, Response.Listener<String>, Response.ErrorListener, LocationUpdater.locationUpdateListener {
    private GoogleMap googleMap;
    protected Message message;
    private HashMap<Marker, MarkerItem> markerHashMap;
    private LocationUpdater locationUpdater;
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Maps
        setupMapifNeeded();
        /*MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);*/
        markerHashMap = new HashMap<>();
        initLocationUpdater();


        // NFC

        setDetecting(true);
    }

    private void setupMapifNeeded() {
        if (googleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }
        if (googleMap != null) {
            updateMap();
        }

    }

    private void initLocationUpdater() {
        locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
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
            setDetecting(false);
            Intent intent = new Intent(MainActivity.this, KilroyNfcTagWriterActivity.class);
            intent.putExtra("lat", googleMap.getMyLocation().getLatitude());
            intent.putExtra("lng", googleMap.getMyLocation().getLongitude());
            startActivityForResult(intent, AppController.NFC_TAG_WRITER_REQUEST);
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void readNdefMessage(Message message) {
        if (message.size() > 1) {
            toast(getString(R.string.readMultipleRecordNDEFMessage));
        } else {
            toast(getString(R.string.readSingleRecordNDEFMessage));
        }

        this.message = message;

        // process message

        // show in log
        // iterate through all records in message
        Log.d("Found " + message.size() + " NDEF records");

        for (int k = 0; k < message.size(); k++) {
            Record record = message.get(k);

            Log.d("Record " + k + " type " + record.getClass().getSimpleName());
            String s = "";
            if (record instanceof MimeRecord) {
                s = new String(record.getNdefRecord().toString());
            } else if (record instanceof ExternalTypeRecord) {
                s = new String(record.getNdefRecord().toString());
            } else if (record instanceof TextRecord) {
                TextRecord textRecord = (TextRecord) record;
                s = textRecord.getText();
                // TODO: Filter Write Intent

                setDetecting(false);
                Intent intent = new Intent(MainActivity.this, PostboardActivity.class);
                intent.putExtra("uuid", s);
                startActivity(intent);

            } else { // more else
                s = new String(record.getNdefRecord().toString());
            }
        }
    }

    @Override
    public void onResponse(String response) {
        Type type = new TypeToken<Collection<PostItem>>() {
        }.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Collection<PostItem> postItems = gson.fromJson(response, type);

        for (MarkerItem item : postItems) {

            Marker marker = googleMap.addMarker(new MarkerOptions().position(item.getMarkerLocation()).title(item.getName()).snippet(item.getDescription()));
            markerHashMap.put(marker, item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMapifNeeded();

    }

    @Override
    protected void readEmptyNdefMessage() {
        toast(getString(R.string.readEmptyMessage));
    }

    @Override
    protected void readNonNdefMessage() {
        toast(getString(R.string.readNonNDEFMessage));
//        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

//        NfcA nfcA = NfcA.get(tag);
//
//        try {
//            nfcA.connect();
//            Short s = nfcA.getSak();
//            byte[] b = nfcA.getAtqa();
//            String msg = new String(b, Charset.forName("UTF-8"));
//            nfcA.close();
//        } catch (IOException e) {
//            Log.d(e.getMessage());
//            toast("Error");
//        }
    }

    @Override
    protected void onNfcStateEnabled() {
        toast(getString(R.string.nfcAvailableEnabled));
    }

    @Override
    protected void onNfcStateDisabled() {
        toast(getString(R.string.nfcAvailableDisabled));
    }

    @Override
    protected void onNfcStateChange(boolean enabled) {
        if (enabled) {
            toast(getString(R.string.nfcAvailableEnabled));
        } else {
            toast(getString(R.string.nfcAvailableDisabled));
        }
    }

    @Override
    protected void onNfcFeatureNotFound() {

    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (googleMap == null)
            googleMap = map;
        updateMap();
    }

    private void updateMap() {

        googleMap.setMyLocationEnabled(true);
        initCamera();
        setMapOnInfoWindowListener();
//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Intent intent = new Intent(MainActivity.this, MarkerDetailActivity.class);
//                MarkerItem item = markerHashMap.get(marker);
//                intent.putExtra("name", item.getName());
//                intent.putExtra("description", item.getDescription());
//                startActivity(intent);
//                return false;
//            }
//        });

        StringRequest request = new StringRequest(AppController.URL + "posts", this, this);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void setMapOnInfoWindowListener() {
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(MainActivity.this, MarkerDetailActivity.class);
                MarkerItem markerItem = markerHashMap.get(marker);
                Log.d("content of markerName after getting it from the hasmap: " + markerItem.getName());
                i.putExtra("name", markerItem.getName());
                i.putExtra("description", markerItem.getDescription());
                startActivity(i);
            }
        });
    }

    private void initCamera() {
        Location location = locationUpdater.getLastKnownLocation();
        if (location != null) {
            Log.d("is location null?" + location.toString());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13);
            googleMap.animateCamera(update);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("VolleyError: ", error != null ? error.toString() : null);
    }

    @Override
    public void onLocationUpdateReceived(Location location) {

    }
}
