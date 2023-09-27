package com.example.demo.model;

import com.mongodb.lang.NonNull;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Getter
@Setter
@Document(collection = "commerce")
public class Product {
    @Id
    private String id;


    @NotNull(message = "name may not be null")
    private String name;

    @NotNull(message = "description may not be null")
    private String description;

    @NotNull
    @Builder.Default
    private int quantity=0;

    @NotNull
    @Builder.Default
    private double price=0;

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date modifiedAt;
}
