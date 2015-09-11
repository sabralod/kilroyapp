package de.ur.mi.kilroy.kilroyapp.items;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by simon on 11/09/15.
 */
public class PostItem implements MarkerItem {
    private int id;
    private String title;
    private String content;
    private Date publishing_date;
    private List<CommentItem> comments;
    private BigDecimal lat;
    private BigDecimal lng;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishing_date() {
        return publishing_date;
    }

    public void setPublishing_date(Date publishing_date) {
        this.publishing_date = publishing_date;
    }

    @Override
    public String getName() {
        return getTitle();
    }

    @Override
    public LatLng getMarkerLocation() {
        LatLng latLng = new LatLng(lat.doubleValue(), lng.doubleValue());
        return latLng;
    }

    @Override
    public String getDescription() {
        return getContent();
    }
}
