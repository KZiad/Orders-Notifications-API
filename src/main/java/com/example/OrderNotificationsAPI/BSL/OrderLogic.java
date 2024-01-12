package com.example.OrderNotificationsAPI.BSL;

import com.example.OrderNotificationsAPI.Controllers.AccountController;
import com.example.OrderNotificationsAPI.Controllers.NotificationController;
import com.example.OrderNotificationsAPI.Models.*;
import com.example.OrderNotificationsAPI.Repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderLogic {
    static final float shippingFee = 29.99F;
    OrderRepository orderRepo;
    NotificationController notificationController = NotificationController.getInstance();
    long lastOrderNumber = 0;

    public OrderLogic(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public CompoundOrder addOrderToCompound(Account account, Product[] products, String address, long compoundOrderNumber) {
        // Get sum of product prices
        float sum = getTotalPrice(products);

        // Check if account has enough money
        if (account.getBalance() < sum) {
            return null;
        }
        account.setBalance(account.getBalance() - sum);

        // Get compound order
        CompoundOrder compoundOrder = (CompoundOrder) orderRepo.getOrder(compoundOrderNumber);

        // Create simple order and add it to compound
        SimpleOrder simpleOrder = new SimpleOrder(account, products, address, compoundOrder.getOrders().size() + 1);
        compoundOrder.addOrder(simpleOrder);

        // Update compound order
        orderRepo.updateOrder(compoundOrder);

        return compoundOrder;
    }


    public CompoundOrder createCompoundOrder(Account account, Product[] products, String address) {
        // Get sum of product prices
        float sum = getTotalPrice(products);

        // Check if account has enough money
        if (account.getBalance() < sum) {
            return null;
        }
        account.setBalance(account.getBalance() - sum);
        AccountController.accountRepository.updateAccount(account);

        // Create compound order and add simple inside it
        CompoundOrder compoundOrder = new CompoundOrder(++lastOrderNumber);
        // order size will be compound internal order number
        long orderSize = orderRepo.getOrders().length;
        SimpleOrder simpleOrder = new SimpleOrder(account, products, address, orderSize + 1);
        compoundOrder.addOrder(simpleOrder);

        orderRepo.addOrder(compoundOrder);


        for (Order o : compoundOrder.getOrders()){
            notificationController.addShipmentNotification(((SimpleOrder) o).getAccount(), compoundOrder.getOrderNumber());
        }

        return compoundOrder;
    }

    private static float getTotalPrice(Product[] products) {
        float sum = 0;
        for (Product product : products) {
            sum += product.getPrice();
        }
        return sum;
    }

    public Order createSimpleOrder(Account account, Product[] products, String address) {
        // Get sum of product prices
        float sum = getTotalPrice(products);

        // Check if account has enough money
        if (account.getBalance() < sum) {
            return null;
        }
        account.setBalance(account.getBalance() - sum);
        AccountController.accountRepository.updateAccount(account);

        // Create order
        Order order = new SimpleOrder(account, products, address, ++lastOrderNumber);

        orderRepo.addOrder(order);
        notificationController.addPlacementNotification(account, products);
        return order;
    }

    public String shipOrder(long orderNumber) {
        Order order = orderRepo.getOrder(orderNumber);

        if (order == null) {
            return null;
        }
        if (order.getStatus().equals("Shipped")) {
           return "Shipped";
        }
        order.setStatus("Shipped");

        if (order instanceof SimpleOrder){
            notificationController.addShipmentNotification(((SimpleOrder) order).getAccount(), orderNumber);
            if (((SimpleOrder) order).getAccount().getBalance() < shippingFee){
                return "Not enough money for shipping";
            }
            Account account = ((SimpleOrder) order).getAccount();
            account.setBalance(account.getBalance() - shippingFee);
            AccountController.accountRepository.updateAccount(account);

        }
        else if (order instanceof CompoundOrder){
            float individualShippingFee = shippingFee / ((CompoundOrder) order).getOrders().size();
            for (Order o : ((CompoundOrder) order).getOrders()){
                Account account = ((SimpleOrder) order).getAccount();
                if (account.getBalance() < individualShippingFee){
                    return "Not enough money for shipping in account " + account.getEmail();
                }
            }
            for (Order o : ((CompoundOrder) order).getOrders()){
                notificationController.addShipmentNotification(((SimpleOrder) o).getAccount(), orderNumber);
                Account account = ((SimpleOrder) order).getAccount();
                account.setBalance(account.getBalance() - individualShippingFee);
                AccountController.accountRepository.updateAccount(account);
            }
        }

        return "Success";
    }

    public Boolean cancelOrder(long orderNumber){
        Order order = orderRepo.getOrder(orderNumber);
        if (new Date().compareTo(order.getPlacementTime()) < order.getCancellationWindow()){
            order.setStatus("Cancelled");
            return true;
        }
        return false;
    }

    public Order getOrder(long orderNumber) {
        return orderRepo.getOrder(orderNumber);
    }

    public Order[] getOrders() {
        return orderRepo.getOrders();
    }
}
