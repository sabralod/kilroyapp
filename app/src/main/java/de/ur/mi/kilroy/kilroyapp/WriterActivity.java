package de.ur.mi.kilroy.kilroyapp;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

// WriterActivity writes a single uuid identifier to a NFC tag, creates a new post and start PostBoardActivity if succeed.
// The parent class NfcTagWriterActivity is used as NFC toolbox for NFC tag creation.

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

//        Activate NFC detection.
        setDetecting(true);

//        View view = findViewById(R.id.writer_infoView);
//        View root = view.getRootView();
//        root.setBackgroundColor(Color.RED);
    }

// Start post board view.
    private void startPostBoard(String uuid) {
        Intent intent = new Intent(this, PostBoardActivity.class);
        intent.putExtra("uuid", uuid.toString());
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setDetecting(false);
    }

    @Override
    public void onResponse(JSONObject response) {
//        deactivate detecting, no more use. Enable foreground.
        setDetecting(false);
        enableForeground();

//        Setup gson.
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

//        Parse json response with gson into PostItem.
        PostItem postItem = gson.fromJson(response.toString(), PostItem.class);

//        If succeed start PostBoardActivity, unlock NFC detection and finish this view.
        if (postItem != null) {
            startPostBoard(postItem.getNfc_id());
            AppController.getInstance().setDetecting(true);
            finish();
            return;
        }
    }

//  Creates a NdefMessage for NFC tag.

    @Override
    protected NdefMessage createNdefMessage() {
        Message message = new Message();

//        add text record with the message
        TextRecord textRecord = new TextRecord();
        textRecord.setKey("uuid");
        textRecord.setText(uuid.toString());
        textRecord.setEncoding(Charset.forName("UTF-8"));
        textRecord.setLocale(Locale.ENGLISH);
        message.add(textRecord);

//        add an Android Application Record so that this app is launches if a tag is scanned :-)
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

//    If writing was successfully, send the user input to backend.

    @Override
    protected void writeNdefSuccess() {
        View view = findViewById(R.id.writer_infoView);
        View root = view.getRootView();

        root.setBackgroundColor(Color.GREEN);
        disableForeground();

//        Disable detection. No more use.
        setDetecting(false);

//        Setup json params.
        HashMap<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("content", content);
        params.put("lat", "" + lat);
        params.put("lng", "" + lng);
        params.put("nfc_id", uuid);

//      Create json request.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(AppController.URL + "posts", new JSONObject(params), this, this);

//        Add json request to request queue in AppController.
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
