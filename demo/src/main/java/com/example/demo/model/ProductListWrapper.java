package com.example.demo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

@XmlRootElement(name = "products")
public class ProductListWrapper {
    private List<Product> products;

    public List<Product> getProducts() {
        return this.products;
    }
    @XmlElement(name = "product")
    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
