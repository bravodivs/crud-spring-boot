package com.example.productservice.util;

import com.example.productservice.model.Product;
import com.example.productservice.model.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ProductUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProductUtils.class);

    private ProductUtils() {
    }

    public static ProductDto productDaoToDto(Product product) {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);
        return productDto;
    }

    public static Product productDtoToDao(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return product;
    }

    public static Boolean validateProduct(ProductDto productDto) {
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(productDto.getName())) {
            errors.put("Name", "Name can't be empty");
        }

        if (!StringUtils.hasText(productDto.getDescription())) {
            errors.put("Description", "Description can't be empty");
        }

        if (productDto.getPrice() != null && productDto.getPrice() <= 0) {
            errors.put("Price", "Price must be a positive value");
        }

        if (productDto.getQuantity() != null && productDto.getQuantity() <= 0) {
            errors.put("Quantity", "Quantity must be a positive value");
        }

        if (CollectionUtils.isEmpty(productDto.getImages())) {
            errors.put("Images", "Images can't be empty");
        } else {
            productDto.getImages().forEach(image -> {
                if (!StringUtils.hasLength(image)) {
                    errors.put("Image", "Image can't be empty");
                }
            });
        }

        if (!errors.isEmpty()) {
            logger.error("Validation Errors: {}", errors);
            return false;
        }

        logger.info("validateProduct: Product object validated successfully");
        return true;
    }
}
