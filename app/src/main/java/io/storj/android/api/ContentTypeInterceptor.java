package io.storj.android.api;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.ArrayList;

public class ContentTypeInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        if (response.getHeaders().get("Content-type") == null) {
            final ArrayList<String> header = new ArrayList<>();
            header.add("application/json");
            response.getHeaders().put("Content-type", header);
        }

        return response;
    }
}
