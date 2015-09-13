package de.ur.mi.kilroy.kilroyapp;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by simon on 11/09/15.
 */
public class AppController extends Application {

    public static final int NFC_TAG_WRITER_REQUEST = 101;
    public static final String TAG = AppController.class.getSimpleName();
    public static final String URL = "http://kilroybackend-kilroybackend.rhcloud.com/api/";
    private static AppController appControllerInstance;
    private RequestQueue requestQueue;

    public static synchronized AppController getInstance() {
        return appControllerInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appControllerInstance = this;

    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        if (tag.isEmpty())
            request.setTag(TAG);
        else
            request.setTag(tag);
        getRequestQueue().add(request);

    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag) {
        if (requestQueue != null)
            requestQueue.cancelAll(tag);
    }
}
