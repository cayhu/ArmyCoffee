package com.example.armycoffee.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.armycoffee.adapters.CartAdapter;
import com.example.armycoffee.databinding.ActivityCartBinding;
import com.example.armycoffee.models.CartItem;
import com.example.armycoffee.utils.CartManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import com.example.armycoffee.utils.VNPayService;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private DecimalFormat formatter = new DecimalFormat("###,###,### VNĐ");
    private VNPayService vnPayService;

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String status = result.getData().getStringExtra("PAYMENT_STATUS");
                if ("SUCCESS".equals(status)) {
                    placeOrder("VNPAY_PAID");
                } else {
                    Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vnPayService = new VNPayService();
        initRecyclerView();
        updateUI();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCheckout.setOnClickListener(v -> showPaymentSelection());
    }

    private void showPaymentSelection() {
        String[] options = {"Tiền mặt (Tại quầy)", "VNPay (Thanh toán ngay)"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setItems(options, (dialog, index) -> {
                    if (index == 0) {
                        placeOrder("COD");
                    } else {
                        startVNPayPayment();
                    }
                })
                .show();
    }

    private void startVNPayPayment() {
        long total = (long) CartManager.getInstance().getTotalPrice();
        vnPayService.getPaymentUrl(total, "Thanh toan don hang Army Coffee", new VNPayService.VnPayCallback() {
            @Override
            public void onSuccess(String paymentUrl) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("PAYMENT_URL", paymentUrl);
                    paymentLauncher.launch(intent);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Lỗi tạo thanh toán: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void initRecyclerView() {
        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateUI);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);
    }

    private void updateUI() {
        double total = CartManager.getInstance().getTotalPrice();
        binding.tvTotalPrice.setText(formatter.format(total));

        if (CartManager.getInstance().getCartItems().isEmpty()) {
            binding.rvCart.setVisibility(View.GONE);
            binding.tvEmptyCart.setVisibility(View.VISIBLE);
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setAlpha(0.5f);
        } else {
            binding.rvCart.setVisibility(View.VISIBLE);
            binding.tvEmptyCart.setVisibility(View.GONE);
            binding.btnCheckout.setEnabled(true);
            binding.btnCheckout.setAlpha(1.0f);
        }
    }

    private void placeOrder(String paymentMethod) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        List<Map<String, Object>> items = new ArrayList<>();
        List<JsonObject> emailItems = new ArrayList<>();
        
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProduct().getId());
            itemMap.put("productName", item.getProduct().getName());
            itemMap.put("price", item.getProduct().getPrice());
            itemMap.put("quantity", item.getQuantity());
            items.add(itemMap);

            JsonObject emailItem = new JsonObject();
            emailItem.addProperty("productName", item.getProduct().getName());
            emailItem.addProperty("quantity", item.getQuantity());
            emailItem.addProperty("price", item.getProduct().getPrice());
            emailItems.add(emailItem);
        }

        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("customerName", userEmail);
        order.put("items", items);
        order.put("totalAmount", CartManager.getInstance().getTotalPrice());
        order.put("status", "pending");
        order.put("createdAt", System.currentTimeMillis());
        order.put("paymentMethod", paymentMethod);

        binding.btnCheckout.setEnabled(false);
        binding.btnCheckout.setText("Đang xử lý...");

        db.collection("orders")
            .add(order)
            .addOnSuccessListener(documentReference -> {
                sendEmailInvoice(userEmail, documentReference.getId(), emailItems, CartManager.getInstance().getTotalPrice());
                CartManager.getInstance().clearCart();
                Toast.makeText(this, "Đặt hàng thành công! Vui lòng kiểm tra email hóa đơn.", Toast.LENGTH_LONG).show();
                finish();
            })
            .addOnFailureListener(e -> {
                binding.btnCheckout.setEnabled(true);
                binding.btnCheckout.setText("Đặt hàng ngay");
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void sendEmailInvoice(String email, String orderId, List<JsonObject> items, double total) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("email", email);
        jsonBody.addProperty("customerName", email.split("@")[0]);
        jsonBody.addProperty("orderId", orderId);
        jsonBody.addProperty("totalAmount", (long)total);
        
        JsonArray itemsArray = new JsonArray();
        for(JsonObject item : items) itemsArray.add(item);
        jsonBody.add("items", itemsArray);

        RequestBody body = RequestBody.create(
            jsonBody.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url("http://10.0.2.2:8080/api/email/invoice")
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {}
        });
    }
}
