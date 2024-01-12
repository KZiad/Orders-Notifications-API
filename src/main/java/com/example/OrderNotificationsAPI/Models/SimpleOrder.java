package com.example.OrderNotificationsAPI.Models;

import com.example.OrderNotificationsAPI.Controllers.AccountController;

import java.util.ArrayList;

public class SimpleOrder extends Order{
    String accountEmail;
    Product[] products;
    String address;

    public SimpleOrder(Account account, Product[] products, String address, long orderNumber) {
        this.orderNumber = orderNumber;
        this.accountEmail = account.getEmail();
        this.products = products;
        this.address = address;
    }
    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public Account getAccount() {
        return AccountController.accountRepository.getAccount(accountEmail);
    }

    public void setAccount(Account account) {
        this.accountEmail = account.getEmail();
    }

    public void setAccount(String accountEmail) {
        this.accountEmail = accountEmail;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
