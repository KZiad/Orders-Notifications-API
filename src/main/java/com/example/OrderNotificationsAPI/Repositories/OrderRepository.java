package com.example.OrderNotificationsAPI.Repositories;

import com.example.OrderNotificationsAPI.Models.CompoundOrder;
import com.example.OrderNotificationsAPI.Models.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class OrderRepository {
    long lastOrderNumber = 0;
    HashMap<Long, Order> Orders = new HashMap<>();

    public OrderRepository() {}

    public OrderRepository(HashMap<Long, Order> Orders) {
        this.Orders = Orders;
    }

    public Order[] getOrders() {
        return Orders.values().toArray(new Order[0]);
    }

    public Order getOrder(Long orderNumber) {
        return Orders.get(orderNumber);
    }

    public void addOrder(Order order) {
        order.setOrderNumber(++lastOrderNumber);
        Orders.put(order.getOrderNumber(), order);
    }

    public int getOrdersCount() {
        return Orders.size();
    }

    public void updateOrder(CompoundOrder compoundOrder) {
        Orders.put(compoundOrder.getOrderNumber(), compoundOrder);
    }
}