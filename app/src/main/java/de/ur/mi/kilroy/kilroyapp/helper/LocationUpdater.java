package de.ur.mi.kilroy.kilroyapp.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by simon on 10/09/15.
 */
public class LocationUpdater implements LocationListener {
    private String locationService;
    private int time;
    private int distance;
    private Context context;

    private static final String provider = LocationManager.GPS_PROVIDER;

    private locationUpdateListener locationReceiver;

    private LocationManager locationManager;


    public LocationUpdater(String locationService, int time, int distance, Context context) {
        this.locationService = locationService;
        this.time = time;
        this.distance = distance;
        this.context = context;
    }

    public void requestLocationUpdates() {
        locationManager = (LocationManager) context.getSystemService(locationService);

        Location location = locationManager.getLastKnownLocation(provider);

        publishLocationUpdate(location);

        locationManager.requestLocationUpdates(provider, time, distance, this); //calls onLocationChanged
    }

    public Location getLastKnownLocation() {
        Location lastLocation = locationManager.getLastKnownLocation(provider);

        return lastLocation;
    }

    public void setLocationUpdateListener(locationUpdateListener receiver) {
        locationReceiver = receiver;
    }

    private void publishLocationUpdate(Location location) {
        if (locationReceiver != null) {
            locationReceiver.onLocationUpdateReceived(location);
        } else {
            Log.d("no location published. locationListener not set");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        publishLocationUpdate(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public interface locationUpdateListener {
        void onLocationUpdateReceived(Location location);
    }
}
