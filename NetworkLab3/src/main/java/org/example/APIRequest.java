package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class APIRequest {
    private final String request_url;
    private final HttpClient client;

    public APIRequest(String request_url, HttpClient client){
        this.request_url = request_url;
        this.client = client;
    }

    public CompletableFuture<String> request(Function<String, String> fn){
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(request_url)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(fn);
    }
}
