package com.example.armycoffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.armycoffee.adapters.ProductAdapter;
import com.example.armycoffee.databinding.ActivityMainBinding;
import com.example.armycoffee.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.armycoffee.utils.OpenAiService;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private FirebaseFirestore db;
    private OpenAiService openAiService;

    private int currentBannerIndex = 0;
    private int[] bannerImages = {
        com.example.armycoffee.R.drawable.coffee_placeholder,
        // Bạn có thể thêm các drawable khác vào đây nếu có
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance("caffee");
        openAiService = new OpenAiService();
        
        initRecyclerView();
        loadProducts();
        setupSearch();
        startBannerAnimation();

        binding.btnProfile.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        binding.cardBooking.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TableSelectionActivity.class));
        });

        binding.cardInvoice.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
        });

        binding.cardTakeaway.setOnClickListener(v -> {
            filter("Cà phê");
            Toast.makeText(this, "Đang hiển thị menu Mang về", Toast.LENGTH_SHORT).show();
        });

        binding.cardMyTable.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TableSelectionActivity.class));
        });

        binding.btnCart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        binding.btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == com.example.armycoffee.R.id.nav_home) {
                // Đã ở trang chủ rồi, có thể cuộn lên đầu trang
                binding.rvProducts.smoothScrollToPosition(0);
                return true;
            } else if (id == com.example.armycoffee.R.id.nav_search) {
                // Focus vào ô tìm kiếm và hiện bàn phím
                binding.etSearch.requestFocus();
                return true;
            } else if (id == com.example.armycoffee.R.id.nav_notifications) {
                Toast.makeText(this, "Tính năng thông báo đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == com.example.armycoffee.R.id.nav_settings) {
                Toast.makeText(this, "Tính năng cài đặt đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void fetchAiSuggestion() {
        if (productList.isEmpty()) return;

        binding.aiCard.setVisibility(View.VISIBLE);
        binding.tvAiSuggestion.setText("Đang suy nghĩ món ngon cho bạn...");

        String menuNames = productList.stream()
                .map(Product::getName)
                .limit(5)
                .collect(Collectors.joining(", "));

        openAiService.getCoffeeSuggestion("Thời tiết hôm nay", menuNames, new OpenAiService.AiCallback() {
            @Override
            public void onSuccess(String suggestion) {
                runOnUiThread(() -> {
                    binding.tvAiSuggestion.setText(suggestion);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.tvAiSuggestion.setText("Hôm nay uống Cà phê sữa nhé! (AI đang bận một chút)");
                });
            }
        });
    }

    private void startBannerAnimation() {
        // Giả lập hiệu ứng đổi banner đơn giản
        binding.bannerCard.setAlpha(0.8f);
        binding.bannerCard.animate().alpha(1.0f).setDuration(1000).start();
    }

    private void seedSampleData() {
        List<Product> samples = new ArrayList<>();
        samples.add(new Product(null, "Cà Phê Muối", 35000, "Cà phê", ""));
        samples.add(new Product(null, "Bạc Xỉu Premium", 32000, "Cà phê", ""));
        samples.add(new Product(null, "Trà Đào Cam Sả", 45000, "Trà", ""));
        samples.add(new Product(null, "Trà Thạch Vải", 42000, "Trà", ""));
        samples.add(new Product(null, "Bánh Croissant", 30000, "Bánh", ""));
        samples.add(new Product(null, "Tiramisu", 55000, "Bánh", ""));
        samples.add(new Product(null, "Matcha Latte", 48000, "Trà", ""));
        samples.add(new Product(null, "Cacao Nóng", 35000, "Khác", ""));

        for (int i = 0; i < samples.size(); i++) {
            Product p = samples.get(i);
            int finalI = i;
            db.collection("products").add(p)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Added product: " + p.getName());
                    if (finalI == samples.size() - 1) {
                        loadProducts();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding product", e));
        }
    }

    private void initRecyclerView() {
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ProductAdapter(filteredList);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            String query = text.toLowerCase().trim();
            for (Product item : productList) {
                if (item.getName().toLowerCase().contains(query) || 
                    item.getCategory().toLowerCase().contains(query)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("products")
            .get()
            .addOnCompleteListener(task -> {
                binding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    productList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                    if (productList.isEmpty()) {
                        seedSampleData(); // Đã kích hoạt để tự động tạo dữ liệu mẫu
                        Toast.makeText(MainActivity.this, "Đang khởi tạo dữ liệu mẫu...", Toast.LENGTH_LONG).show();
                    }
                    filter(""); // Hiển thị tất cả ban đầu
                    fetchAiSuggestion(); // Bắt đầu lấy gợi ý từ AI
                } else {
                    Log.e("Firebase", "Error getting products: ", task.getException());
                    Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
