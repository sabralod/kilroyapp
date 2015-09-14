package de.ur.mi.kilroy.kilroyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.ur.mi.kilroy.kilroyapp.adapters.CommentItemAdapter;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class PostboardActivity extends ListActivity implements Response.Listener<String>, Response.ErrorListener {

    private TextView nameView;
    private TextView descriptionView;
    private String uuid = "";
    private String post_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postboard);

        nameView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);

        Intent i = getIntent();
        uuid = i.getStringExtra("uuid");

        updateData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postboard_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_comment) {
            if (!post_id.isEmpty()) {
                Intent intent = new Intent(PostboardActivity.this, CommentActivity.class);
                intent.putExtra("post_id", "" + post_id);
                startActivityForResult(intent, AppController.CREATE_COMMENT_REQUEST);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {

        if (!uuid.isEmpty()) {
            StringRequest request = new StringRequest(AppController.URL + "post/uuid/" + uuid, this, this);
            AppController.getInstance().addToRequestQueue(request);
        } else if (!post_id.isEmpty()) {
            StringRequest request = new StringRequest(AppController.URL + "post/id/" + post_id, this, this);
            AppController.getInstance().addToRequestQueue(request);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppController.CREATE_COMMENT_REQUEST && resultCode == AppController.CREATE_COMMENT_REQUEST_DONE) {
            post_id = data.getStringExtra("post_id");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("VolleyError: ", error.toString());
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PostItem postItem = gson.fromJson(response, PostItem.class);
        post_id = "" + postItem.getId();
        uuid = postItem.getNfc_id();
        nameView.setText(postItem.getTitle());
        descriptionView.setText(postItem.getDescription());

        ListView listView = getListView();
        listView.setAdapter(new CommentItemAdapter(this, R.layout.item_comment, postItem.getComments()));

    }
}
