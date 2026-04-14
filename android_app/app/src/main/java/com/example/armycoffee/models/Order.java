package com.example.armycoffee.models;

import java.util.List;
import java.util.Map;

public class Order {
    private String id;
    private String userId;
    private String customerName;
    private List<Map<String, Object>> items; // Chứa thông tin món uống và số lượng
    private double totalAmount;
    private String status; // pending, processing, completed, cancelled
    private long createdAt;
    private String paymentMethod; // COD, VNPay

    public Order() {
    }

    public Order(String id, String userId, String customerName, List<Map<String, Object>> items, double totalAmount, String status, long createdAt, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
