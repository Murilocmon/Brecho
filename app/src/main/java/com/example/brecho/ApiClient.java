package com.example.brecho;

import okhttp3.*;

public class ApiClient {
    private static final String BASE_URL = "https://seudominio.epizy.com/api/";
    private static final OkHttpClient client = new OkHttpClient();

    public static void post(String endpoint, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
