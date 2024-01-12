package com.example.OrderNotificationsAPI.Models;

import java.util.ArrayList;

public class CompoundOrder extends Order{
    ArrayList<Order> orders;

    public CompoundOrder(long orderNumber) {
        this.orderNumber = orderNumber;
        orders = new ArrayList<Order>();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        for (Order order : orders) {
            order.setStatus(status);
        }
    }

}
