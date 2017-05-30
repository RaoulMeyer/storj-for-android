package io.storj.android.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.storj.android.R;

public class FileImageFragment extends Fragment {
    private static final String ARG_FILE_CONTENT = "fileContent";

    private byte[] fileContents;

    public FileImageFragment() {
    }

    public static FileImageFragment newInstance(byte[] fileContent) {
        FileImageFragment fragment = new FileImageFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_FILE_CONTENT, fileContent);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileContents = getArguments().getByteArray(ARG_FILE_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_file_image, container, false);

//        final Bitmap bitmap = BitmapFactory.decodeByteArray(fileContents, 0, fileContents.length);
        final Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.icon);

        final ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageBitmap(bitmap);

        return view;
    }
}
