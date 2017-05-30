package io.storj.android.api;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.RequiresAuthentication;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

import io.storj.android.api.models.Bucket;
import io.storj.android.api.models.BucketCreateRequest;
import io.storj.android.api.models.FileShard;
import io.storj.android.api.models.Frame;
import io.storj.android.api.models.FrameForBucketRequest;
import io.storj.android.api.models.ShardForFrameRequest;
import io.storj.android.api.models.UserRegisterRequest;
import io.storj.android.api.models.UserRegisterResponse;

@Rest(
        converters = MappingJackson2HttpMessageConverter.class,
        rootUrl = "https://api.storj.io",
        interceptors = { LoggingInterceptor.class, ContentTypeInterceptor.class }
)
public interface StorjApiInterface {
    @Post("/users")
    UserRegisterResponse registerUser(@Body UserRegisterRequest userRegisterRequest);

    @Delete("/users/{email}")
    @RequiresAuthentication
    void deleteUser(@Path String email);

    @Get("/buckets")
    @RequiresAuthentication
    List<Bucket> getBuckets();

    @Get("/buckets/{id}/files")
    @RequiresAuthentication
    List<io.storj.android.api.models.File> getFilesInBucket(@Path String id);

    @Post("/buckets")
    @RequiresAuthentication
    void createBucket(@Body BucketCreateRequest bucketCreateRequest);

    @Get("/buckets/{bucketId}/files/{fileId}")
    @RequiresAuthentication
    List<FileShard> getFileShards(@Path String bucketId, @Path String fileId);

    @Post("/frames")
    @RequiresAuthentication
    Frame createFrame();

    @Put("/frames/{frameId}")
    @RequiresAuthentication
    FileShard addShardToFrame(@Path String frameId, @Body ShardForFrameRequest shardForFrameRequest);

    @Post("/buckets/{bucketId}/files")
    @RequiresAuthentication
    void storeFrameInBucket(@Path String bucketId, @Body FrameForBucketRequest frameForBucketRequest);

    void setHttpBasicAuth(String username, String password);
}
