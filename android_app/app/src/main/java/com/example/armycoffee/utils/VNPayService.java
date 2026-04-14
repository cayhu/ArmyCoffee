package com.example.armycoffee.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.*;

public class VNPayService {
    private static final String BACKEND_URL = "http://10.0.2.2:8080/api/payment/vnpay-url";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface VnPayCallback {
        void onSuccess(String paymentUrl);
        void onFailure(String error);
    }

    public void getPaymentUrl(long amount, String orderInfo, VnPayCallback callback) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("amount", String.valueOf(amount));
        jsonBody.addProperty("orderInfo", orderInfo);

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
                    String paymentUrl = root.get("paymentUrl").getAsString();
                    callback.onSuccess(paymentUrl);
                } else {
                    callback.onFailure("Lỗi tạo URL thanh toán: " + response.code());
                }
            }
        });
    }
}
