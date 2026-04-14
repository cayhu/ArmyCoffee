package com.example.armycoffee.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.armycoffee.R;
import com.example.armycoffee.models.Table;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public TableAdapter(List<Table> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.tvNumber.setText("Bàn " + table.getNumber());
        
        String statusText = "Trống";
        int iconRes = R.drawable.ic_table_available;
        int statusColor = Color.parseColor("#4CAF50");

        if ("booked".equals(table.getStatus())) {
            statusText = "Đã đặt";
            iconRes = R.drawable.ic_table_booked;
            statusColor = Color.parseColor("#FF9800");
        } else if ("occupied".equals(table.getStatus())) {
            statusText = "Đang dùng";
            iconRes = R.drawable.ic_table_occupied;
            statusColor = Color.parseColor("#F44336");
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(statusColor);
        holder.ivStatus.setImageResource(iconRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTableClick(table);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus;
        TextView tvNumber, tvStatus;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivTableStatus);
            tvNumber = itemView.findViewById(R.id.tvTableNumber);
            tvStatus = itemView.findViewById(R.id.tvTableStatus);
        }
    }
}
