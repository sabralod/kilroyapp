package de.ur.mi.kilroy.kilroyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;

import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.CommentItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 14/09/15.
 */
public class CommentActivity extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText authorEditText;
    private EditText contentEditText;

    private String post_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        authorEditText = (EditText) findViewById(R.id.commentAuthorEditText);
        contentEditText = (EditText) findViewById(R.id.commentContentEditText);

        post_id = getIntent().getStringExtra("post_id");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CommentItem postItem = gson.fromJson(response.toString(), CommentItem.class);
        if (postItem != null) {
            Intent intent = getIntent();
            intent.putExtra("post_id", "" + postItem.getPost_id());
            setResult(AppController.CREATE_COMMENT_REQUEST_DONE, intent);
            finish();
        }
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
            final String author = authorEditText.getText().toString();
            final String content = contentEditText.getText().toString();

            HashMap<String, String> params = new HashMap<>();
            params.put("post_id", post_id);
            params.put("author", author);
            params.put("content", "" + content);

            JsonObjectRequest request = new JsonObjectRequest(AppController.URL + "posts/id/" + post_id + "/comments", new JSONObject(params), this, this);
            AppController.getInstance().addToRequestQueue(request);
        }

        return super.onOptionsItemSelected(item);
    }
}
