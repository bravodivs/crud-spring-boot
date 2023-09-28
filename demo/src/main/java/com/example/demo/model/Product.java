package com.example.demo.model;

import com.mongodb.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Document(collection = "commerce")
public class Product {

    public interface ValidationGroupOne{};
    public interface ValidationGroupTwo{};

    @Id
//    @NotBlank(groups = ValidationGroupOne.class)
    private String id;


    @NotBlank(message = "name may not be empty"
//            , groups = ValidationGroupTwo.class
            )
    private String name;

    @NotBlank(message = "description may not be empty")
    private String description;

    @NotNull
    @Builder.Default
    private int quantity=0;

    @NotNull
    @Builder.Default
    private double price=0;

    @NotNull
    private List<@NotEmpty String> images;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date modifiedAt;
}
