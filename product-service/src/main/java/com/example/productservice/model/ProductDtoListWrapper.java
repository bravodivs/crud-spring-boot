package com.example.productservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "products")
public class ProductDtoListWrapper {
    private List<ProductDto> productDtoList;

    public List<ProductDto> getProductDtoList() {
        return this.productDtoList;
    }
    @XmlElement(name = "product")
    public void setProductDtoList(List<ProductDto> productDtoList) {
        this.productDtoList = productDtoList;
    }

}
