package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private String id;

    @NotNull(message = "Name may not be null")
    @NotBlank(message = "Name may not be empty")
    private String name;

    @NotNull(message = "Description may not be null")
    @NotBlank(message = "Description may not be empty")
    private String description;

    @NotNull(message = "Quantity may not be empty")
    @Positive(message = "Quantity may not be negative")
    private Integer quantity;

    @NotNull(message = "Price may not be null")
    @Positive(message = "Price may not be negative")
    private Double price;

    @NotNull(message = "Images may not be null")
    @NotEmpty(message = "Images may not be empty")
    private List<@NotEmpty(message = "Image may not be empty") String> images;

    @Immutable
    private Date createdAt;
    private Date modifiedAt;

    public ProductDto(String name, String description, Integer quantity, Double price, List<String> images){
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setImages(images);
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }
}
