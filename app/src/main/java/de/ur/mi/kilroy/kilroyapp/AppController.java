package de.ur.mi.kilroy.kilroyapp;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by simon on 11/09/15.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController appControllerInstance;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        appControllerInstance = this;
    }

    public static synchronized AppController getInstance() {
        return appControllerInstance;
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
