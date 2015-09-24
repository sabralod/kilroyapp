package de.ur.mi.kilroy.kilroyapp.items;

import com.google.android.gms.maps.model.LatLng;


public interface MarkerItem {
    String getName();

    LatLng getMarkerLocation();

    String getDescription();
}
