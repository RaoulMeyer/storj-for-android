package io.storj.android.bucket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.storj.android.R;
import io.storj.android.api.StorjService;
import io.storj.android.api.models.Bucket;
import io.storj.android.util.Function;

@EActivity
public class BucketActivity extends AppCompatActivity {

    @Bean
    public StorjService storjService;

    @ViewById
    public ListView bucketList;

    private List<Bucket> buckets;

    private boolean updating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBucketCreateDialog();
            }
        });

        startBucketUpdate();
    }

    @AfterViews
    public void init() {
        buckets = new ArrayList<>();
        final ArrayAdapter<Bucket> arrayAdapter = new BucketAdapter(this, android.R.layout.simple_list_item_1, buckets);
        bucketList.setAdapter(arrayAdapter);
        bucketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bucket bucket = buckets.get(position);
                BucketDetailActivity_.intent(BucketActivity.this)
                        .extra("bucket_id", bucket.id)
                        .extra("bucket_name", bucket.name)
                        .start()
                        .withAnimation(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        startBucketUpdate();
    }

    private void startBucketUpdate() {
        if (updating) {
            return;
        }

        updating = true;
        storjService.getBuckets(new Function<List<Bucket>>() {
            @Override
            public void call(List<Bucket> buckets) {
                updateBucketList(buckets);
                updating = false;
            }
        });
    }


    @UiThread
    public void updateBucketList(List<Bucket> buckets) {
        ArrayAdapter<Bucket> adapter = (ArrayAdapter<Bucket>) bucketList.getAdapter();

        this.buckets.clear();
        this.buckets.addAll(buckets);

        adapter.notifyDataSetChanged();
    }

    private void showBucketCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BucketActivity.this);
        builder.setTitle("Create a new bucket");

        final EditText input = new EditText(BucketActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                storjService.createBucket(name, new Function<Void>() {
                    @Override
                    public void call(Void argument) {
                        startBucketUpdate();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
