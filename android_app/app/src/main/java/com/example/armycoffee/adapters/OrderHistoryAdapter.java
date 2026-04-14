package com.example.armycoffee.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.armycoffee.R;
import com.example.armycoffee.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<Order> orders;

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("#" + order.getId().substring(0, 8).toUpperCase());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvOrderDate.setText(sdf.format(new Date(order.getCreatedAt())));
        
        StringBuilder itemsStr = new StringBuilder();
        for (Map<String, Object> item : order.getItems()) {
            itemsStr.append(item.get("productName")).append(" x ").append(item.get("quantity")).append(", ");
        }
        if (itemsStr.length() > 2) itemsStr.setLength(itemsStr.length() - 2);
        holder.tvOrderItems.setText(itemsStr.toString());
        
        holder.tvTotalAmount.setText(String.format("%,.0f VNĐ", order.getTotalAmount()));
        
        holder.tvOrderStatus.setText(order.getStatus());
        // Tùy chỉnh màu sắc dựa trên trạng thái (ví dụ)
        if ("pending".equals(order.getStatus())) {
            holder.tvOrderStatus.setBackgroundResource(R.drawable.status_pending_bg);
        } else if ("completed".equals(order.getStatus())) {
            holder.tvOrderStatus.setBackgroundResource(R.drawable.status_completed_bg);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderItems, tvTotalAmount, tvOrderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
