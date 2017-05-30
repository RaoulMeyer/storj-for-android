package io.storj.android.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bucket {
    public int storage;
    public int transfer;
    public String status;
    public List<String> pubkeys;
    public String user;
    public String name;
    public String created;
    public String id;

    public String toString() {
        return name;
    }
}
