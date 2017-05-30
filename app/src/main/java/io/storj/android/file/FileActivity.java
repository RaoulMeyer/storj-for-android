package io.storj.android.file;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import io.storj.android.R;
import io.storj.android.api.StorjService;
import io.storj.android.util.Function;

@EActivity
public class FileActivity extends AppCompatActivity {

    @Bean
    public StorjService storjService;

    private String fileId;
    private String bucketId;
    private String mimetype;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        getSupportActionBar().setTitle(intent.getStringExtra("file_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileId = intent.getStringExtra("file_id");
        bucketId = intent.getStringExtra("bucket_id");
        mimetype = intent.getStringExtra("mimetype");

        fetchFileContents();
    }

    @AfterViews
    public void init() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading file");
        progress.setCancelable(false);
        progress.show();

        fetchFileContents();
    }

    @Background
    public void fetchFileContents() {
        if (bucketId == null || fileId == null) {
            return;
        }

        storjService.getFile(bucketId, fileId, new Function<byte[]>() {
            @Override
            public void call(byte[] argument) {
                processFileContents(argument);
            }
        });
    }

    @UiThread
    public void processFileContents(byte[] contents) {

        LinearLayout fragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        ll.setId(R.id.parent);

        if (mimetype.contains("image")) {
            getSupportFragmentManager().beginTransaction().add(ll.getId(), FileImageFragment.newInstance(contents), "imageContent").commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(ll.getId(), FileTextFragment.newInstance(contents), "textContent").commit();
        }

        fragmentContainer.addView(ll);

        progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
