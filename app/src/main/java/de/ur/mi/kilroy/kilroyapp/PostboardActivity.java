package de.ur.mi.kilroy.kilroyapp;

import android.app.ListActivity;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.Collection;

import de.ur.mi.kilroy.kilroyapp.adapters.CommentItemAdapter;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class PostboardActivity extends ListActivity implements Response.Listener<String>, Response.ErrorListener {

    private TextView nameView;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postboard);

        nameView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);

        initPostAdapter();

    }

    private void initPostAdapter() {
        Intent i = getIntent();
        String s = i.getStringExtra("uuid");
        StringRequest request = new StringRequest(AppController.URL + "post/" + s, this, this);
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("VolleyError: ", error.getMessage());
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PostItem postItem = gson.fromJson(response, PostItem.class);

        nameView.setText(postItem.getTitle());
        descriptionView.setText(postItem.getDescription());

        ListView listView = getListView();
        listView.setAdapter(new CommentItemAdapter(this, R.layout.item_comment, postItem.getComments()));

    }
}
