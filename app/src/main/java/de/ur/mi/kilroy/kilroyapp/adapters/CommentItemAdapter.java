package de.ur.mi.kilroy.kilroyapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import de.ur.mi.kilroy.kilroyapp.R;
import de.ur.mi.kilroy.kilroyapp.items.CommentItem;


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
        View v;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_comment, null);
        } else {
            v = convertView;
        }

        TextView author = (TextView) v.findViewById(R.id.author_textView);
        TextView date = (TextView) v.findViewById(R.id.date_textView);
        TextView content = (TextView) v.findViewById(R.id.content_textView);

        CommentItem item = (CommentItem) this.objects.get(position);

        author.setText(item.getAuthor());
        date.setText(item.getSubmission_date().toString());
        content.setText(item.getContent());

        return v;
    }
}
