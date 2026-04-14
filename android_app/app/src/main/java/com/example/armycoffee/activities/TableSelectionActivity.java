package com.example.armycoffee.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.armycoffee.adapters.TableAdapter;
import com.example.armycoffee.databinding.ActivityTableSelectionBinding;
import com.example.armycoffee.models.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableSelectionActivity extends AppCompatActivity {

    private ActivityTableSelectionBinding binding;
    private TableAdapter adapter;
    private List<Table> tableList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTableSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        initRecyclerView();
        loadTables();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        tableList = new ArrayList<>();
        adapter = new TableAdapter(tableList, table -> {
            if ("available".equals(table.getStatus())) {
                showBookingConfirmDialog(table);
            } else {
                Toast.makeText(this, "Bàn này không khả dụng", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvTables.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvTables.setAdapter(adapter);
    }

    private void loadTables() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("tables")
            .addSnapshotListener((value, error) -> {
                binding.progressBar.setVisibility(View.GONE);
                if (error != null) {
                    Log.w("Firebase", "Listen failed.", error);
                    return;
                }
                tableList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Table table = doc.toObject(Table.class);
                    table.setId(doc.getId());
                    tableList.add(table);
                }
                adapter.notifyDataSetChanged();
            });
    }

    private void showBookingConfirmDialog(Table table) {
        new AlertDialog.Builder(this)
            .setTitle("Đặt bàn")
            .setMessage("Bạn có muốn đặt Bàn số " + table.getNumber() + " không?")
            .setPositiveButton("Xác nhận", (dialog, which) -> {
                sendBookingRequest(table);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void sendBookingRequest(Table table) {
        String userId = FirebaseAuth.getInstance().getUid();
        String customerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (customerName == null || customerName.isEmpty()) customerName = "Khách hàng";

        Map<String, Object> booking = new HashMap<>();
        booking.put("tableId", table.getId());
        booking.put("tableNumber", table.getNumber());
        booking.put("userId", userId);
        booking.put("customerName", customerName);
        booking.put("status", "pending");
        booking.put("createdAt", System.currentTimeMillis());
        booking.put("time", "Hôm nay");

        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(TableSelectionActivity.this, "Yêu cầu đặt bàn đã được gửi!", Toast.LENGTH_LONG).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(TableSelectionActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
