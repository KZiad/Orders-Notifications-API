package com.example.OrderNotificationsAPI.Models;

import java.util.Date;

public abstract class Order {

    long orderNumber;
    String status = "Pending";
    Date placementTime;
    final int cancellationWindow = 300000; // 5 minutes in ms
    public Order(){
        placementTime = new Date();
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPlacementTime() {
        return placementTime;
    }

    public int getCancellationWindow() {
        return cancellationWindow;
    }
}