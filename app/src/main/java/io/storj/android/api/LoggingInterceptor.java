package io.storj.android.api;

import android.util.Log;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] data, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, data);
        ClientHttpResponse response = execution.execute(request, data);

        if (response.getStatusCode() != HttpStatus.OK) {
            logResponseHeaders(response);
            logResponse(response);
        }

        return response;
    }

    private void logRequest(HttpRequest request, byte[] data) {
        Log.d("DEBUG", request.getURI().toString());
        try {
            Log.d("DEBUG", new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void logResponseHeaders(ClientHttpResponse response) {
        for (String key : response.getHeaders().keySet()) {
            System.out.println(key + " = " + response.getHeaders().get(key));
        }
    }

    private void logResponse(ClientHttpResponse response) {
        try {
            final InputStream body = response.getBody();
            Scanner scanner = new Scanner(body).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "";

            Log.d("DEBUG", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}