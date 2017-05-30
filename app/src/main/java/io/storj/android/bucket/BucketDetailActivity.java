package io.storj.android.bucket;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.storj.android.R;
import io.storj.android.api.StorjService;
import io.storj.android.api.models.Bucket;
import io.storj.android.api.models.File;
import io.storj.android.file.FileActivity_;
import io.storj.android.file.FileAdapter;
import io.storj.android.util.Function;

@EActivity
public class BucketDetailActivity extends AppCompatActivity {

    @Bean
    public StorjService storjService;

    private String bucketId;
    private String bucketName;

    @ViewById
    public ListView fileList;

    private List<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bucket_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        bucketId = intent.getStringExtra("bucket_id");
        bucketName = intent.getStringExtra("bucket_name");

        setTitle(bucketName);
        System.out.println(bucketName);

        startBucketContentUpdate();
    }

    @Click
    public void fab() {
        if(ContextCompat.checkSelfPermission(BucketDetailActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BucketDetailActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            new MaterialFilePicker()
                    .withActivity(BucketDetailActivity.this)
                    .withRequestCode(1)
                    .withHiddenFiles(false)
                    .withPath("/")
                    .start();
        }
    }

    @Click
    public void fabImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Select pictures to upload");
        startActivityForResult(chooser, 2);
    }

    @AfterViews
    public void init() {
        files = new ArrayList<>();
        final ArrayAdapter<File> arrayAdapter = new FileAdapter(this, android.R.layout.simple_list_item_1, files);
        fileList.setAdapter(arrayAdapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file = files.get(position);
                FileActivity_.intent(BucketDetailActivity.this)
                        .extra("file_id", file.id)
                        .extra("bucket_id", bucketId)
                        .extra("file_name", file.filename)
                        .extra("mimetype", file.mimetype)
                        .start()
                        .withAnimation(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        startBucketContentUpdate();
    }

    private void startBucketContentUpdate() {
        if (bucketId == null) {
            return;
        }

        storjService.getBucketContents(bucketId, new Function<List<File>>() {
            @Override
            public void call(List<File> files) {
                updateFileList(files);
            }
        });
    }

    @UiThread
    public void updateFileList(List<File> files) {
        ArrayAdapter<Bucket> adapter = (ArrayAdapter<Bucket>) fileList.getAdapter();

        this.files.clear();
        this.files.addAll(files);

        adapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            System.out.println(filePath);
        }

        if (requestCode == 2) {
            if (data.getData() == null) {
                ClipData clipdata = data.getClipData();
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    System.out.println(getFilePathFromContentUri(clipdata.getItemAt(i).getUri(), getContentResolver()));
                }
            } else {
                System.out.println(getFilePathFromContentUri(data.getData(), getContentResolver()));
            }
        }
    }

    private String getFilePathFromContentUri(Uri uri, ContentResolver contentResolver) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);

        assert cursor != null;
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }
}
