package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "product")
@Getter
@Setter
@Document(collection = "commerce")
public class Product {
    @Id
    private String id;

    private String name;

    private String description;

    private Integer quantity;

    private Double price;

    private List<String> images;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date modifiedAt;
}
