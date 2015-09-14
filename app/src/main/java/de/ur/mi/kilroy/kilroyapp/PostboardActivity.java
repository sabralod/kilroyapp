package de.ur.mi.kilroy.kilroyapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import de.ur.mi.kilroy.kilroyapp.adapters.CommentItemAdapter;
import de.ur.mi.kilroy.kilroyapp.adapters.PostItemAdapter;
import de.ur.mi.kilroy.kilroyapp.items.PostItem;

/**
 * Created by simon on 13/09/15.
 */
public class PostboardActivity extends ListActivity {

    private TextView nameView;
    private TextView descriptionView;
    private int id;
    private PostItemAdapter postItemAdapter;
    private PostItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postboard);
        initUi();
        setupListview();
        initPostAdapter();

    }

    private void initPostAdapter() {
        Intent i = getIntent();
        String s = i.getStringExtra("uuid");
        postItemAdapter = new PostItemAdapter(s);
        item = postItemAdapter.getPostItem();


    }


    private void initUi() {
        nameView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);


        nameView.setText(item.getTitle());
        descriptionView.setText(item.getDescription());

    }

    private void setupListview() {
        ListView listView =  getListView();
        listView.setAdapter(new CommentItemAdapter(this,R.layout.item_comment,item.getComments() ));
    }
}
