package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;


@XmlRootElement(name="product")
@Getter
@Setter
@Document(collection = "commerce")
public class Product {

//    @XmlAttribute(name = "id")
    @Id
    private String id;


//    @XmlElement(name = "name")
    @NotNull(message = "Name may not be null")
    @NotBlank(message = "Name may not be empty")
    private String name;

//    @XmlAttribute
    @NotNull(message = "Description may not be null")
    @NotBlank(message = "Description may not be empty")
    private String description;

//    @XmlElement
    @NotNull(message = "Quantity may not be empty")
    @Positive(message = "Quantity may not be negative")
    private int quantity;

//    @XmlElement
    @NotNull(message = "Price may not be null")
    @Positive(message = "Price may not be negative")
    private double price;

//    @XmlElement
    @NotNull(message = "Images may not be null")
    @NotEmpty(message = "Images may not be empty")
    private List<@NotEmpty(message = "Image may not be empty") String> images;

//    @XmlElement
    @CreatedDate
    @Immutable
    private Date createdAt;

//    @XmlElement
    @LastModifiedDate
    private Date modifiedAt;
}
