package de.ur.mi.kilroy.kilroyapp;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.util.activity.NfcReaderActivity;
import org.ndeftools.wellknown.TextRecord;

import de.ur.mi.kilroy.kilroyapp.helper.Log;

/**
 * Created by simon on 12/09/15.
 */
public class KilroyNfcReaderActivity extends NfcReaderActivity {
    public static final String TAG = AppController.class.getSimpleName();

    protected Message message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.d(TAG, "Found " + message.size() + " NDEF records");

        for (int k = 0; k < message.size(); k++) {
            Record record = message.get(k);

            Log.d(TAG, "Record " + k + " type " + record.getClass().getSimpleName());

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

        // show in gui
//        showList();
    }

    @Override
    protected void readEmptyNdefMessage() {
        toast(getString(R.string.readEmptyMessage));

//        clearList();
    }

    @Override
    protected void readNonNdefMessage() {
        toast(getString(R.string.readNonNDEFMessage));

//        hideList();
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
        toast(getString(R.string.noNfcMessage));
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
