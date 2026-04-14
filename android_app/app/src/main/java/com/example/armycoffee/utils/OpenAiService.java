package com.example.armycoffee.utils;

import android.util.Log;
import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;

public class OpenAiService {
    private static final String BACKEND_URL = "http://10.0.2.2:8080/api/ai/suggest"; // 10.0.2.2 là localhost của máy tính khi chạy Emulator
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface AiCallback {
        void onSuccess(String suggestion);
        void onFailure(String error);
    }

    public void getCoffeeSuggestion(String weather, String menuItems, AiCallback callback) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("weather", weather);
        jsonBody.addProperty("menu", menuItems);

        RequestBody body = RequestBody.create(
            jsonBody.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(BACKEND_URL)
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
                    String suggestion = root.get("suggestion").getAsString();
                    callback.onSuccess(suggestion);
                } else {
                    callback.onFailure("Lỗi Backend: " + response.code());
                }
            }
        });
    }
}
