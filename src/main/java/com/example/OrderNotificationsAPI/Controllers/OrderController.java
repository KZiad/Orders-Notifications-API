package com.example.OrderNotificationsAPI.Controllers;

import com.example.OrderNotificationsAPI.BSL.OrderLogic;
import com.example.OrderNotificationsAPI.Models.*;
import com.example.OrderNotificationsAPI.Repositories.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/order")
public class OrderController {
    public final static OrderLogic orderBSL = new OrderLogic(new OrderRepository());
    @GetMapping("/get/{orderNumber}")
    public ResponseEntity<Order> getOrder(@PathVariable long orderNumber) {
        Order order = orderBSL.getOrder(orderNumber);
        if (order != null){
            return ResponseEntity.ok().body(order);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<Order[]> getOrders(@RequestParam(value = "status", required = false) String status) {
        Order[] list = orderBSL.getOrders();
        if (status != null){
            ArrayList<Order> filteredList = new ArrayList<>();
            for (Order order : list){
                if (order.getStatus().equals(status)){
                    filteredList.add(order);
                }
            }
            return ResponseEntity.ok().body(filteredList.toArray(new Order[0]));
        }

        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/ship")
    public ResponseEntity<Object> shipOrder(@RequestParam(value = "orderNumber") long orderNumber) {
        String status = orderBSL.shipOrder(orderNumber);
        if (status == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order doesn't exist");
        }
        else if (status.equals("Shipped")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already Shipped");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(orderBSL.getOrder(orderNumber));
        }
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(@RequestParam(value = "orderNumber") long orderNumber) {
        if (orderBSL.cancelOrder(orderNumber)){
            return ResponseEntity.status(HttpStatus.OK).body(orderBSL.getOrder(orderNumber));
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(orderBSL.getOrder(orderNumber));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createSimpleOrder(@RequestParam(value = "email") String email,
                                                   @RequestParam(value = "address") String address,
                                                   @RequestBody long[] productIds) {
        Account account = AccountController.accountRepository.getAccount(email);
        if (account == null) {
            return new ResponseEntity<>("Account not found with Email " + email, null, 404);
        }

        address = address.replace("%20", " ");
        ArrayList<Product> products = new ArrayList<>();

        for (long id : productIds) {
            Product product = ProductController.productRepository.getProduct(id);
            if (product == null) {
                return new ResponseEntity<>("Product not found with ID " + id, null, 404);
            }
            products.add(product);
        }

        Product[] temp = new Product[products.size()];
        Order order = orderBSL.createSimpleOrder(account, products.toArray(temp), address);

        if (order == null){
            return ResponseEntity.status(HttpStatusCode.valueOf(402)).build();
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(order);
    }

    @PostMapping("/compound/create")
    public ResponseEntity<Object> createCompoundOrder(@RequestParam(value = "email") String email,
                                                       @RequestParam(value = "address") String address,
                                                       @RequestBody long[] productIds){
        Account account = AccountController.accountRepository.getAccount(email);
        if (account == null) {
            return ResponseEntity.status(404).body("Account not found with Email " + email);
        }
        address = address.replace("%20", " ");
        Product[] products = new Product[productIds.length];
        for (int i = 0; i < productIds.length; i++) {
            Product product = ProductController.productRepository.getProduct(productIds[i]);
            if (product == null) {
                return ResponseEntity.status(404).body("Product not found with ID " + productIds[i]);
            }
            products[i] = product;
        }

        Order order = orderBSL.createCompoundOrder(account, products, address);
        if (order == null){
            return ResponseEntity.status(402).body("Insufficient funds");
        }
        return ResponseEntity.status(201).body(order);
    }
    @PutMapping("/compound/addOrder")
    public ResponseEntity<Object> addOrder(@RequestParam(value = "email") String email,
                                              @RequestParam(value = "orderNumber") long orderNumber,
                                              @RequestParam(value = "address") String address,
                                              @RequestBody long[] productIds) {
        Account account = AccountController.accountRepository.getAccount(email);
        if (account == null) {
            return ResponseEntity.status(404).body("Account not found with Email " + email);
        }
        address = address.replace("%20", " ");
        Product[] products = new Product[productIds.length];
        for (int i = 0; i < productIds.length; i++) {
            Product product = ProductController.productRepository.getProduct(productIds[i]);
            if (product == null) {
                return ResponseEntity.status(404).body("Product not found with ID " + productIds[i]);
            }
            products[i] = product;
        }

        if (orderBSL.getOrder(orderNumber) == null){
            return ResponseEntity.status(404).body("Order not found with ID " + orderNumber);
        }
        if (orderBSL.getOrder(orderNumber).getStatus().equals("Shipped")){
            return ResponseEntity.status(403).body("Order already shipped");
        }
        if (orderBSL.getOrder(orderNumber).getStatus().equals("Cancelled")){
            return ResponseEntity.status(403).body("Cannot modify cancelled order");
        }
        CompoundOrder compOrder = orderBSL.addOrderToCompound(account,  products, address, orderNumber);
        return ResponseEntity.status(201).body(compOrder);
    }

    @GetMapping("/getStatistics")
    public ResponseEntity<String> getStatistics() {
        ArrayList<String> dataArray = NotificationController.getInstance().getStatistics();

        String data = "Most frequently used template = " + dataArray.get(0) + ", used = " + dataArray.get(1) + "\n" +
                "Most frequent account = " + dataArray.get(2) + ", ordered = " + dataArray.get(3);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

}
