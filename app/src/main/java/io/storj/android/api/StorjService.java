package io.storj.android.api;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import io.storj.android.api.models.Bucket;
import io.storj.android.api.models.BucketCreateRequest;
import io.storj.android.api.models.File;
import io.storj.android.api.models.FileShard;
import io.storj.android.api.models.Frame;
import io.storj.android.api.models.FrameForBucketRequest;
import io.storj.android.api.models.ShardForFrameRequest;
import io.storj.android.api.models.UserRegisterRequest;
import io.storj.android.api.models.UserRegisterResponse;
import io.storj.android.crypto.RIPEMD;
import io.storj.android.crypto.SHA256;
import io.storj.android.persistence.PreferenceStore;
import io.storj.android.util.Function;

@EBean
public class StorjService {
    @RestService
    public StorjApiInterface storjApi;

    @Bean
    public PreferenceStore preferenceStore;

    private String passwordHash;
    private String username;

    @AfterInject
    public void init() {
        username = preferenceStore.getString("username");
        passwordHash = preferenceStore.getString("password");

        storjApi.setHttpBasicAuth(username, passwordHash);
    }

    public void updateAuthData(String username, String password) {
        String passwordHash = SHA256.hash(password);
        storjApi.setHttpBasicAuth(username, passwordHash);
    }

    @Background
    public void testUserLogin(String username, String password, Function<Boolean> callback) {
        String passwordHash = SHA256.hash(password);
        storjApi.setHttpBasicAuth(username, passwordHash);
        boolean success;
        try {
            storjApi.getBuckets();
            success = true;
        } catch(HttpClientErrorException e) {
            e.printStackTrace();
            success = false;
        }
        storjApi.setHttpBasicAuth(null, null);
        callback.call(success);
    }

    @Background
    public void registerUser() {

        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.email = username;
        userRegisterRequest.password = passwordHash;
//        userRegisterRequest.pubkey = publicKey;

        final UserRegisterResponse userRegisterResponse = storjApi.registerUser(userRegisterRequest);

        System.out.println(userRegisterResponse.email);
        System.out.println(userRegisterResponse.created);
        System.out.println(userRegisterResponse.activated);
    }

    @Background
    public void deleteUser() {
        storjApi.deleteUser(username);
    }

    @Background
    public void getBuckets(Function<List<Bucket>> callback) {
        final List<Bucket> buckets = storjApi.getBuckets();

        for (Bucket bucket : buckets) {
            System.out.println(bucket.name);
        }

        callback.call(buckets);
    }

    @Background
    public void getBucketContents(String bucketId, Function<List<File>> callback) {
        final List<io.storj.android.api.models.File> filesInBucket = storjApi.getFilesInBucket(bucketId);

        for (File file : filesInBucket) {
            System.out.println(file.filename);
        }

        callback.call(filesInBucket);
    }

    @Background
    public void createBucket(String bucketName, Function<Void> callback) {
        final BucketCreateRequest bucketCreateRequest = new BucketCreateRequest();
        bucketCreateRequest.name = bucketName;
        bucketCreateRequest.pubkeys = new ArrayList<>();

        storjApi.createBucket(bucketCreateRequest);

        callback.call(null);
    }

    public void getFile(String bucketId, String fileId, Function<byte[]> callback) {
        System.out.println(bucketId);
        System.out.println(fileId);

        final List<FileShard> fileShards = storjApi.getFileShards(bucketId, fileId);

        ArrayList<Byte> fileData = new ArrayList<>();

        // TODO: Sort shards by index?

        for (FileShard fileShard : fileShards) {
            String shardUrl = fileShard.getUrl();

            try {
                final OkHttpClientHttpRequestFactory requestFactory = new OkHttpClientHttpRequestFactory();
                final ClientHttpRequest request = requestFactory.createRequest(URI.create(shardUrl), HttpMethod.GET);
                final ClientHttpResponse response = request.execute();
                final InputStream body = response.getBody();

                Scanner scanner = new Scanner(body).useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";

                for (byte b : result.getBytes()) {
                    fileData.add(b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] fileDataPrimitive = new byte[fileData.size()];

        for (int i = 0; i < fileData.size(); i++) {
            fileDataPrimitive[i] = fileData.get(i);
        }

        System.out.println(new String(fileDataPrimitive));
        callback.call(fileDataPrimitive);
    }

    public void putFile(String bucketId, String filePath) {
        final Frame frame = storjApi.createFrame();
        String frameId = frame.id;

        byte[][] shards = new byte[0][];

        try {
            final byte[] bytes = FileUtils.readFileToByteArray(new java.io.File(filePath));
            shards = splitBytes(bytes, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<FileShard> fileShards = new ArrayList<>();

        int index = 0;
        for (byte[] shard : shards) {
            final FileShard fileShard = getFileShard(frameId, index, shard);
            fileShards.add(fileShard);
            index++;
        }

        index = 0;
        for (FileShard fileShard : fileShards) {
            final String shardUrl = fileShard.getUrl();

            try {
                final OkHttpClient client = new OkHttpClient();

                final RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), shards[index]);

                System.out.println(Arrays.toString(shards[index]));

                Request request = new Request.Builder()
                        .url(shardUrl)
                        .post(body)
                        .build();

                final Response response = client.newCall(request).execute();

                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }

            index++;
        }

        final FrameForBucketRequest frameForBucketRequest = new FrameForBucketRequest();
        frameForBucketRequest.filename = "test123.txt";
        frameForBucketRequest.frame = frameId;
        frameForBucketRequest.mimetype = "text/plain";

        storjApi.storeFrameInBucket(bucketId, frameForBucketRequest);
    }

    private FileShard getFileShard(String frameId, int index, byte[] shard) {
        boolean success;
        FileShard fileShard = null;
        do {
            try {
                final ShardForFrameRequest shardForFrameRequest = new ShardForFrameRequest();
                shardForFrameRequest.index = index;
                shardForFrameRequest.size = shard.length;
                shardForFrameRequest.hash = RIPEMD.hashWithSha(new String(shard));
                shardForFrameRequest.challenges.add("2128bc38ed5140bb9ba8ddac16183eecc4c9ef63b0cd46b30f49b578737a7a52");
                shardForFrameRequest.tree.add("507f1f77bcf86cd799439011");

                fileShard = storjApi.addShardToFrame(frameId, shardForFrameRequest);
                success = true;
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                success = false;
                System.out.println("Retrying...");
            }
        } while (!success);

        return fileShard;
    }

    private byte[][] splitBytes(final byte[] data, final int chunkSize)
    {
        final int length = data.length;
        final byte[][] dest = new byte[(length + chunkSize - 1)/chunkSize][];
        int destIndex = 0;
        int stopIndex = 0;

        for (int startIndex = 0; startIndex + chunkSize <= length; startIndex += chunkSize) {
            stopIndex += chunkSize;
            dest[destIndex++] = Arrays.copyOfRange(data, startIndex, stopIndex);
        }

        if (stopIndex < length) {
            dest[destIndex] = Arrays.copyOfRange(data, stopIndex, length);
        }

        return dest;
    }
}
