package de.ur.mi.kilroy.kilroyapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import org.ndeftools.Record;
import org.ndeftools.wellknown.TextRecord;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.ur.mi.kilroy.kilroyapp.helper.LocationUpdater;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Response.Listener<String>, Response.ErrorListener, LocationUpdater.locationUpdateListener {
    private GoogleMap googleMap;
    private HashMap<Marker, MarkerItem> markerHashMap;
    private LocationUpdater locationUpdater;
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Maps
        setupMapIfNeeded();
        markerHashMap = new HashMap<>();
        initLocationUpdater();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // NFC
        resolveIntent(getIntent());
        AppController.getInstance().setDetecting(true);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (AppController.getInstance().isDetecting()) {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                handleTag(rawMsgs);

            } else {
                Log.d("Unknown intent " + intent);
                return;
            }
        }
    }

    private void handleTag(Parcelable[] rawMsgs) {
        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
        } else {
            // Unknown tag type
            byte[] empty = new byte[]{};
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
            NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
            msgs = new NdefMessage[]{msg};
        }

        if (msgs == null || msgs.length == 0) {
            Log.d("No Messages " + getIntent());
            return;
        }

        List<Record> records;
        try {
            records = new Message(msgs[0]);
            final int size = records.size();

            String uuid = "";

            for (Record record :
                    records) {
                if (record instanceof TextRecord) {
                    TextRecord textRecord = (TextRecord) record;
                    if (textRecord.hasKey()) {
                        if (textRecord.getKey().equals("uuid"))
                            uuid = textRecord.getText();
                    }
                }
            }

            if (uuid != "")
                startPostboard(uuid);
        } catch (FormatException e) {
            toast("Reading failed.");
        }
    }

    private void startPostboard(String uuid) {
        AppController.getInstance().setDetecting(false);
        Intent postboardIntent = new Intent(MainActivity.this, PostboardActivity.class);
        postboardIntent.putExtra("uuid", uuid);
        startActivity(postboardIntent);
    }


    private void setupMapIfNeeded() {
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

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_write_tag) {
            AppController.getInstance().setDetecting(true);
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            intent.putExtra("lat", googleMap.getMyLocation().getLatitude());
            intent.putExtra("lng", googleMap.getMyLocation().getLongitude());
            startActivity(intent);
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
        setupMapIfNeeded();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
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
