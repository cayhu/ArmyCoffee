package com.example.armycoffee.models;

public class Table {
    private String id;
    private int number;
    private String status; // available, booked, occupied
    private int capacity;

    public Table() {
    }

    public Table(String id, int number, String status, int capacity) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
