package de.ur.mi.kilroy.kilroyapp;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class AppController extends Application {

    public static final int NFC_TAG_WRITER_REQUEST = 101;
    public static final String TAG = AppController.class.getSimpleName();
    public static final String URL = "http://kilroybackend-kilroybackend.rhcloud.com/api/";
    public static final int CREATE_COMMENT_REQUEST = 301;
    public static final int CREATE_COMMENT_REQUEST_DONE = 302;
    private static AppController appControllerInstance;
    private RequestQueue requestQueue;
    private boolean detecting = false;

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

    public static String getPlayIdentifier() {
        PackageInfo pi;
        try {
            pi = appControllerInstance.getPackageManager().getPackageInfo(appControllerInstance.getPackageName(), 0);
            return pi.applicationInfo.packageName;
        } catch (final PackageManager.NameNotFoundException e) {
            return appControllerInstance.getClass().getPackage().getName();
        }
    }

    public void setDetecting(boolean detecting) {
        this.detecting = detecting;
    }

    public boolean isDetecting() {
        return detecting;
    }
}
