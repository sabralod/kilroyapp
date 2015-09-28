package de.ur.mi.kilroy.kilroyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// CreatePostActivity hold the view for creating a new post on a tag.
// First get the user input, then start WriterActivity for NFC tag writing and backend request.

public class CreatePostActivity extends AppCompatActivity {

    private double lat;
    private double lng;
    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditView);
        initButton();
    }

    private void initButton() {
        Button createButton = (Button) findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWrite();
            }
        });
    }

//    Gives the user input to WriterActivity, start WriterActivity and finish this view.
    private void startWrite() {
        final String title = titleEditText.getText().toString();
        final String content = contentEditText.getText().toString();

        Intent intent = new Intent(this, WriterActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("lat", "" + lat);
        intent.putExtra("lng", "" + lng);
        startActivity(intent);
        finish();
        return;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Intent intent = new Intent(CreatePostActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}