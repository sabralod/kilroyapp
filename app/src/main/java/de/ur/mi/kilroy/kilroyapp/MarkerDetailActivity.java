package de.ur.mi.kilroy.kilroyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            if (id == R.id.action_help) {
                Intent intent = new Intent(MarkerDetailActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

