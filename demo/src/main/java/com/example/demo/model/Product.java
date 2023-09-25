package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "commerce")
public class Product {
    @Id
    private String id;

    private String name;

    private String description;

    private int quantity;

    private double price;
}
