package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

//primary key of type string
public interface ProductRepository extends MongoRepository<Product, String> {
    public List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String productKey, String descriptionKey
    );
}
