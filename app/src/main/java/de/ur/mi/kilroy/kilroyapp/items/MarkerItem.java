package de.ur.mi.kilroy.kilroyapp.items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by simon on 10/09/15.
 */
public class MarkerItem {
    private String name;
    private LatLng markerLocation;
    private String description;
    private int id;


    public MarkerItem(String name, LatLng markerLocation, String description, int id) {
        this.name = name;
        this.markerLocation = markerLocation;
        this.description = description;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public LatLng getMarkerLocation() {
        return markerLocation;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
