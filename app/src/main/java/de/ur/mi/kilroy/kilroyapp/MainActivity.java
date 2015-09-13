package de.ur.mi.kilroy.kilroyapp;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class MainActivity extends NfcReaderActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    protected Message message;
    private HashMap<Marker, MarkerItem> markerHashMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Maps

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        markerHashMap = new HashMap<>();

        // NFC

        setDetecting(true);
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
            intent.putExtra("lat", googleMap.getMyLocation());
            intent.putExtra("lng", googleMap.getMyLocation());
            startActivityForResult(intent, AppController.NFC_TAG_WRITER_REQUEST);
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

                // TODO: Start PostboardActivity here

            } else { // more else
                s = new String(record.getNdefRecord().toString());
            }

            toast(s);
        }
    }

    @Override
    protected void readEmptyNdefMessage() {
        toast(getString(R.string.readEmptyMessage));
    }

    @Override
    protected void readNonNdefMessage() {
        toast(getString(R.string.readNonNDEFMessage));
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

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
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO: Start MarkerDetailActivity here.
                Intent intent = new Intent(MainActivity.this, MarkerDetailActivity.class);
                MarkerItem item = markerHashMap.get(marker);
                intent.putExtra("name",item.getName());
                intent.putExtra("description", item.getDescription());
                startActivity(intent);
                return false;
            }
        });

        StringRequest request = new StringRequest(AppController.URL + "posts", new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError: ", error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

}
