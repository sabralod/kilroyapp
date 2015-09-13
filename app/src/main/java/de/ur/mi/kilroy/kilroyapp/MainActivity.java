package de.ur.mi.kilroy.kilroyapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.util.activity.NfcReaderActivity;
import org.ndeftools.wellknown.TextRecord;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;

import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class MainActivity extends NfcReaderActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] ndefIntentFilters;
    protected Message message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Maps

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // NFC

        nfcAdapter = nfcAdapter.getDefaultAdapter(this);

        nfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefIntentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord();
            androidApplicationRecord.setPackageName(AppController.getPlayIdentifier());
            ndefIntentFilter.addDataType(androidApplicationRecord.toString());
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.d(e.getMessage());
        }

        ndefIntentFilters = new IntentFilter[]{ndefIntentFilter,};

        setDetecting(true);
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

            // your own code here, for example:
            if (record instanceof MimeRecord) {
                // ..
            } else if (record instanceof ExternalTypeRecord) {
                // ..
            } else if (record instanceof TextRecord) {
                // ..
            } else { // more else
                // ..
            }
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

        NfcA nfcA = NfcA.get(tag);

        try {
            nfcA.connect();
            Short s = nfcA.getSak();
            byte[] b = nfcA.getAtqa();
            String msg = new String(b, Charset.forName("UTF-8"));
            nfcA.close();
        } catch (IOException e) {
            Log.d(e.getMessage());
            toast("Error");
        }
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

    //    void resolveIntent(Intent intent) {
//        // Parse the intent
//        String action = intent.getAction();
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
//            // When a tag is discovered we send it to the service to be save. We
//            // include a PendingIntent for the service to call back onto. This
//            // will cause this activity to be restarted with onNewIntent(). At
//            // that time we read it from the database and view it.
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage[] msgs;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else {
//                // Unknown tag type
//                byte[] empty = new byte[]{};
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
//                msgs = new NdefMessage[]{msg};
//            }
//        } else {
//            Log.d("Unknown intent " + intent);
//            finish();
//            return;
//        }
//    }

//    @Override
//    public void onNewIntent(Intent intent) {
////        setIntent(intent);
////        resolveIntent(intent);
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//            Log.d("Here i am.");
//            NdefMessage[] ndefMessages = getNdefMessages(intent);
//        }
//    }

//    NdefMessage[] getNdefMessages(Intent intent) {
//        // Parse the intent
//        NdefMessage[] msgs = null;
//        String action = intent.getAction();
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable[] rawMsgs =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else {
//                // Unknown tag type
//                byte[] empty = new byte[]{};
//                NdefRecord record =
//                        new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
//                msgs = new NdefMessage[]{msg};
//            }
//        } else {
//            Log.d("Unknown intent.");
//            finish();
//        }
//        return msgs;
//    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (googleMap == null)
            googleMap = map;
        updateMap();
    }

    private void updateMap() {

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        StringRequest request = new StringRequest(AppController.URL + "posts", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Collection<PostItem> postItems = gson.fromJson(response, type);

                for (MarkerItem item : postItems) {
                    googleMap.addMarker(new MarkerOptions().position(item.getMarkerLocation()).title(item.getName()).snippet(item.getDescription()));
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent,
//                ndefIntentFilters, null);
//    }
}
