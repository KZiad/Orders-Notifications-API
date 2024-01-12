package com.example.OrderNotificationsAPI.Controllers;

import com.example.OrderNotificationsAPI.Models.Product;
import com.example.OrderNotificationsAPI.Repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    public static final ProductRepository productRepository = new ProductRepository();

    @GetMapping(value = "/getAll")
    public ResponseEntity<ArrayList<Product>> getProducts(){
        ArrayList<Product> list = productRepository.getProducts();
        if (list != null){
            return ResponseEntity.ok().body(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        if (productRepository.addProduct(product)){
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping(value = "/get/{serialNumber}")
    public ResponseEntity<Product> getProduct(@PathVariable("serialNumber") long serialNumber){
        Product product = productRepository.getProduct(serialNumber);
        if (product != null){
            return ResponseEntity.ok().body(product);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/getCount")
    public ResponseEntity<Integer> getCount(@RequestParam("category") String category){
        return ResponseEntity.ok(productRepository.getCategoryCount(category));
    }

    @DeleteMapping (value = "/delete/{serialNumber}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("serialNumber") long serialNumber){
        if (productRepository.deleteProduct(serialNumber)){
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}