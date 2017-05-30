package io.storj.android.file;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.storj.android.R;

public class FileTextFragment extends Fragment {
    private static final String ARG_TEXT = "param1";

    private String text;

    public FileTextFragment() {
    }

    public static FileTextFragment newInstance(byte[] fileContent) {
        FileTextFragment fragment = new FileTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, new String(fileContent));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_file_text, container, false);

        final TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(text);

        return view;
    }

}
