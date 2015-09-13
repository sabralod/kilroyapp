package de.ur.mi.kilroy.kilroyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by simon on 13/09/15.
 */
public class PostboardActivity extends ListActivity {

    private TextView nameView;
    private TextView descriptionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postboard);
        initUi();

    }

    private void initUi() {
        nameView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);

        Intent i = getIntent();
        nameView.setText(i.getStringExtra("name"));
        descriptionView.setText(i.getStringExtra("description"));
    }
}
