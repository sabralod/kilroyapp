package de.ur.mi.kilroy.kilroyapp.items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by simon on 10/09/15.
 */
public interface MarkerItem {
    String getName();

    LatLng getMarkerLocation();

    String getDescription();
}
