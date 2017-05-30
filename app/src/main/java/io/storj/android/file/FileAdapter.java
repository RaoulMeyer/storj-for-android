package io.storj.android.file;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.storj.android.R;
import io.storj.android.api.models.File;

public class FileAdapter extends ArrayAdapter<File> {

    private final LayoutInflater layoutInflater;
    private final List<File> data;

    public FileAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<File> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_file, null);
        }

        final File file = data.get(position);

        TextView textName = (TextView) view.findViewById(R.id.fileName);
        textName.setText(file.filename);

        TextView textSize = (TextView) view.findViewById(R.id.fileSize);
        textSize.setText(longToSizeString(file.size));

        ImageView icon = (ImageView) view.findViewById(R.id.fileIcon);
        icon.setImageResource(file.getIconId());

        return view;
    }

    private String longToSizeString(long input) {
        String[] orders = new String[] {
                "B",
                "KB",
                "MB",
                "GB"
        };

        double temp = input;
        int order = -1;
        while (temp > 0.5d) {
            temp /= 1024d;
            order++;
        }

        temp *= 1024d;

        if (temp > 10) {
            return String.valueOf((int) temp) + orders[order];
        }

        return String.valueOf(temp) + orders[order];
    }
}
