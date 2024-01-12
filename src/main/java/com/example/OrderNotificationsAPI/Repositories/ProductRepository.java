package com.example.OrderNotificationsAPI.Repositories;


import java.util.ArrayList;
import java.util.HashMap;
import com.example.OrderNotificationsAPI.Models.Product;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    HashMap<Long, Product> Products = new HashMap<>();

    public ArrayList<Product> getProducts() {
        ArrayList<Product> list = new ArrayList<Product>(Products.values());
        if (!list.isEmpty()){
            return list;
        }
        return null;
    }

    public Product getProduct(Long serialNumber) {
        if (Products.containsKey(serialNumber)){
            return Products.get(serialNumber);
        }
        return null;
    }

    public Boolean addProduct(Product product) {
        if (!Products.containsKey(product.getSerialNumber())){
            product.setCategory(product.getCategory().replace(' ', '_').toLowerCase());
            Products.put(product.getSerialNumber(), product);
            return true;
        }
        return false;
    }

    public int getProductsCount() {
        return Products.size();
    }

    public Boolean deleteProduct(Long serialNumber) {
        if (Products.containsKey(serialNumber)) {
            Products.remove(serialNumber);
            return true;
        }
        return false;
    }

    public int getCategoryCount(String category) {
        int count = 0;
        for (Product product : Products.values()) {
            if (product.getCategory().equals(category)) {
                count++;
            }
        }
        return count;
    }
}