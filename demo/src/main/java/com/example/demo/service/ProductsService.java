package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ProductsService {
    @Autowired
    ProductRepository products;

    public List<Product> getAllProducts() {
        List<Product> pr = products.findAll();
        return pr;
    }

    public Product getProductById(int id) throws ResourceNotFoundException {
        return products.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not present with id "+id, null));
    }

    public List<Product> saveProduct(Product product) {
        products.save(product);
        return getAllProducts();
    }
    public List<Product> saveAllProducts(List<Product> prodList){
        products.saveAll(prodList);
        return getAllProducts();
    }

    public List<Product> deleteProduct(int id) throws ResourceNotFoundException {

        Product p = getProductById(id);
        products.deleteById(id);
        return getAllProducts();
    }

    public ResponseEntity<Product> updateProduct(int id){
    return null;
    }
}
