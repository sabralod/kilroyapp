package de.ur.mi.kilroy.kilroyapp;

import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.ndeftools.Message;
import org.ndeftools.util.activity.NfcTagWriterActivity;
import org.ndeftools.wellknown.TextRecord;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 12/09/15.
 */
public class KilroyNfcTagWriterActivity extends NfcTagWriterActivity {
    public static final int NFC_TAG_WRITER_DONE = 200;

    private double lat;
    private double lng;

    private EditText titleEditText;
    private EditText contentEditText;

    private UUID uuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_writer);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditView);

        uuid = UUID.randomUUID();
    }

    @Override
    protected NdefMessage createNdefMessage() {
        // compose our own message
        Message message = new Message();

        // add an Android Application Record so that this app is launches if a tag is scanned :-)
        // add a Text Record with the message which is entered
        TextRecord textRecord = new TextRecord();
        textRecord.setKey("uuid");
        textRecord.setText(uuid.toString());
        textRecord.setEncoding(Charset.forName("UTF-8"));
        textRecord.setLocale(Locale.ENGLISH);
        message.add(textRecord);

        return message.getNdefMessage();
    }

    @Override
    protected void writeNdefFailed(Exception e) {
        toast(getString(R.string.ndefWriteFailed, e.toString()));
    }

    @Override
    protected void writeNdefNotWritable() {
        toast(getString(R.string.tagNotWritable));
    }

    @Override
    protected void writeNdefTooSmall(int required, int capacity) {
        toast(getString(R.string.tagTooSmallMessage, required, capacity));
    }

    @Override
    protected void writeNdefCannotWriteTech() {
        toast(getString(R.string.cannotWriteTechMessage));
    }

    @Override
    protected void writeNdefSuccess() {
        toast(getString(R.string.ndefWriteSuccess));
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
            toast(getString(R.string.nfcSettingEnabled));
        } else {
            toast(getString(R.string.nfcSettingDisabled));
        }
    }

    @Override
    protected void onNfcFeatureNotFound() {
        toast(getString(R.string.noNfcMessage));
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

            final String title = titleEditText.getText().toString();
            final String content = contentEditText.getText().toString();
            final String nfc_id = uuid.toString();

            HashMap<String, String> params = new HashMap<>();
            params.put("title", title);
            params.put("content", content);
            params.put("lat", "" + lat);
            params.put("lng", "" + lng);
            params.put("nfc_id", nfc_id);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(AppController.URL + "posts", new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    PostItem postItem = gson.fromJson(response.toString(), PostItem.class);
                    if (postItem != null) {
                        setDetecting(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError: ", error.getMessage());
                }
            });


            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        }

        return super.onOptionsItemSelected(item);
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
