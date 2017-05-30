package io.storj.android.api.models;

public class FileShard {
    public String hash;
    public String token;
    public String operation;
    public String index;
    public long size;
    public boolean parity;
    public Farmer farmer;

    public String getUrl() {
        return "http://" + farmer.address + ":" + farmer.port + "/shards/" + hash + "?token=" + token;
    }
}
