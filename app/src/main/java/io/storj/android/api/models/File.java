package io.storj.android.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.storj.android.R;

@JsonIgnoreProperties(ignoreUnknown = true)
public class File {
    public String id;
    public String bucket;
    public String mimetype;
    public String filename;
    public long size;
    public String frame;
//    public Hmac hmac;

    public String toString() {
        return filename;
    }

    public int getIconId() {
        if (mimetype.contains("image")) {
            return R.drawable.file_image;
        }

        return R.drawable.file_default;
    }
}
