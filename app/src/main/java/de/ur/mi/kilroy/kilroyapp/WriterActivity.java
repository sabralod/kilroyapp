package de.ur.mi.kilroy.kilroyapp;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.ndeftools.Message;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.util.activity.NfcTagWriterActivity;
import org.ndeftools.wellknown.TextRecord;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 27/09/15.
 */
public class WriterActivity extends NfcTagWriterActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private String title;
    private String content;
    private String lat;
    private String lng;
    private String uuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);


        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        uuid = UUID.randomUUID().toString();

        setDetecting(true);

        View view = findViewById(R.id.writer_infoView);
        View root = view.getRootView();

        //root.setBackgroundColor(Color.RED);



    }


    private void startPostBoard(String uuid) {
        Intent intent = new Intent(this, PostboardActivity.class);
        intent.putExtra("uuid", uuid.toString());
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setDetecting(false);
    }

    @Override
    public void onResponse(JSONObject response) {
        setDetecting(false);
        enableForeground();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PostItem postItem = gson.fromJson(response.toString(), PostItem.class);
        if (postItem != null) {
            finish();
            startPostBoard(postItem.getNfc_id());
            return;
        }
    }

    @Override
    protected NdefMessage createNdefMessage() {
        // compose our own message
        Message message = new Message();

        // add a Text Record with the message which is entered
        TextRecord textRecord = new TextRecord();
        textRecord.setKey("uuid");
        textRecord.setText(uuid.toString());
        textRecord.setEncoding(Charset.forName("UTF-8"));
        textRecord.setLocale(Locale.ENGLISH);
        message.add(textRecord);

        // add an Android Application Record so that this app is launches if a tag is scanned :-)
        AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord();
        androidApplicationRecord.setPackageName(AppController.getPlayIdentifier());
        message.add(androidApplicationRecord);

        return message.getNdefMessage();
    }

    @Override
    protected void writeNdefFailed(Exception e) {
        setDetecting(false);
        toast(getString(R.string.ndefWriteFailed, e.toString()));
    }

    @Override
    protected void writeNdefNotWritable() {
        setDetecting(false);
        toast(getString(R.string.tagNotWritable));
    }

    @Override
    protected void writeNdefTooSmall(int required, int capacity) {
        setDetecting(false);
        toast(getString(R.string.tagTooSmallMessage, required, capacity));
    }

    @Override
    protected void writeNdefCannotWriteTech() {
        setDetecting(false);
        toast(getString(R.string.cannotWriteTechMessage));
    }

    @Override
    protected void writeNdefSuccess() {
        View view = findViewById(R.id.writer_infoView);
        View root = view.getRootView();

        root.setBackgroundColor(Color.GREEN);
        disableForeground();

        AppController.getInstance().setDetecting(false);
        setDetecting(false);

        HashMap<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("content", content);
        params.put("lat", "" + lat);
        params.put("lng", "" + lng);
        params.put("nfc_id", uuid);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(AppController.URL + "posts", new JSONObject(params), this, this);

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        toast(getString(R.string.ndefWriteSuccess));
    }

    @Override
    protected void onNfcStateEnabled() {
        //  toast(getString(R.string.nfcAvailableEnabled));
    }

    @Override
    protected void onNfcStateDisabled() {
        setDetecting(false);
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

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
