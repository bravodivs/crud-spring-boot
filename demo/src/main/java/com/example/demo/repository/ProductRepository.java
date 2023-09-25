package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

//primary key of type int
public interface ProductRepository extends MongoRepository<Product, Integer> {
}
