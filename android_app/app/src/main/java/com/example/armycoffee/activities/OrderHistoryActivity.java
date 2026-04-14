package com.example.armycoffee.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.armycoffee.R;
import com.example.armycoffee.adapters.OrderHistoryAdapter;
import com.example.armycoffee.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(orderList);
        rvOrderHistory.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        orderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId());
                            orderList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (orderList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
