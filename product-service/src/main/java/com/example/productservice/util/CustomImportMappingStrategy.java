package com.example.productservice.util;

import com.example.productservice.model.ProductDto;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CustomImportMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

private static final Logger logger = LoggerFactory.getLogger(CustomImportMappingStrategy.class);
    protected String[] getHeader() {
        return new String[]{"name", "description", "price", "quantity", "images"};
    }
    @Override
    public T populateNewBean(String[] line) throws CsvBeanIntrospectionException{
        ProductDto productDto = new ProductDto();
        productDto.setName(line[0]);
        productDto.setDescription(line[1]);
        productDto.setPrice(Double.parseDouble(line[2]));
        productDto.setQuantity(Integer.parseInt(line[3]));
        productDto.setImages(List.of(line[4].split(",\\s*")));
        logger.info("Product mapped");
        return (T) productDto;
    }
}
