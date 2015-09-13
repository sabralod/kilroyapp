package de.ur.mi.kilroy.kilroyapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import de.ur.mi.kilroy.kilroyapp.AppController;
import de.ur.mi.kilroy.kilroyapp.helper.Log;
import de.ur.mi.kilroy.kilroyapp.items.CommentItem;
import de.ur.mi.kilroy.kilroyapp.items.MarkerItem;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class PostItemAdapter {

    private PostItem postItem;

    public PostItem getPostItem() {
        return postItem;
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    public PostItemAdapter(String uuid) {
        if (uuid != null && !uuid.isEmpty()) {
            StringRequest request = new StringRequest(AppController.URL + "post/" + uuid, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Type type = new TypeToken<Collection<PostItem>>() {
                    }.getType();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    postItem = gson.fromJson(response, PostItem.class);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError: ", error.getMessage());
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        }
    }
}
