package io.storj.android.bucket;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.storj.android.R;
import io.storj.android.api.models.Bucket;

public class BucketAdapter extends ArrayAdapter<Bucket> {

    private final LayoutInflater layoutInflater;
    private final List<Bucket> data;

    public BucketAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Bucket> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_bucket, null);
        }

        TextView text = (TextView) view.findViewById(R.id.bucketName);
        text.setText(data.get(position).name);

        return view;
    }
}
