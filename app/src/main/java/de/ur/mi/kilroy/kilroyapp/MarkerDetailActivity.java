package de.ur.mi.kilroy.kilroyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import de.ur.mi.kilroy.kilroyapp.helper.Log;


public class MarkerDetailActivity extends Activity {
    private TextView markerNameView;
    private TextView markerDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markerdetail);
        initUi();
        setupViews();
    }

    private void initUi() {
        this.markerNameView = (TextView) findViewById(R.id.view_markerName);
        this.markerDescriptionView = (TextView) findViewById(R.id.view_markerDescription);
    }

    private void setupViews() {
        Intent intent = getIntent();
        String tagName = intent.getStringExtra("name");
        String tagDescription = intent.getStringExtra("description");
        Log.d("content of string tagName in detailActivity: " + tagName);
        markerNameView.setText(tagName);
        markerDescriptionView.setText(tagDescription);
    }
}
