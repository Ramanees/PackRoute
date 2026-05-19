package com.example.swiftdelivery.agent;

public class Earning {
    private String deliveryId;
    private String date;
    private double amount;

    public Earning() {}

    public Earning(String deliveryId, String date, double amount) {
        this.deliveryId = deliveryId;
        this.date = date;
        this.amount = amount;
    }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
