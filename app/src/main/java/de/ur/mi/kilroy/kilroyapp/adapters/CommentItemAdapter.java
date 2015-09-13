package de.ur.mi.kilroy.kilroyapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import de.ur.mi.kilroy.kilroyapp.items.CommentItem;

/**
 * Created by simon on 13/09/15.
 */
public class CommentItemAdapter extends ArrayAdapter<CommentItem> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private List objects;

    public CommentItemAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CommentItemAdapter(Context context, int resource, List<CommentItem> objects) {
        super(context, resource, objects);

        this.resource = resource;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
    }
}
