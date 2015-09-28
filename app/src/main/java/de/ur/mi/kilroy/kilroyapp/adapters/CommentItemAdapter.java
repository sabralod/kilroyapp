package de.ur.mi.kilroy.kilroyapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import de.ur.mi.kilroy.kilroyapp.R;
import de.ur.mi.kilroy.kilroyapp.items.CommentItem;

// CommentItemAdapter handles the list view items of PostBoardActivity.

public class CommentItemAdapter extends ArrayAdapter<CommentItem> {

    private Context context;
    private List objects;

    public CommentItemAdapter(Context context, int resource, List<CommentItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_comment, null);
        } else {
            v = convertView;
        }

//        Fill view.

        TextView author = (TextView) v.findViewById(R.id.author_textView);
        TextView date = (TextView) v.findViewById(R.id.date_textView);
        TextView content = (TextView) v.findViewById(R.id.content_textView);

        CommentItem item = (CommentItem) this.objects.get(position);

        DateFormat format = DateFormat.getDateInstance();

        String dateSmall = format.format(item.getSubmission_date());

        author.setText(item.getAuthor());
        date.setText(dateSmall);
        content.setText(item.getContent());

        return v;
    }
}
