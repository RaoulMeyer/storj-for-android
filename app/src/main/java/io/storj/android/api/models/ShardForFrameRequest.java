package io.storj.android.api.models;

import java.util.ArrayList;
import java.util.List;

public class ShardForFrameRequest {
    public String hash;
    public long size;
    public int index;
    public List<String> challenges = new ArrayList<>();
    public List<String> tree = new ArrayList<>();
//    public List<String> exclude;
}
