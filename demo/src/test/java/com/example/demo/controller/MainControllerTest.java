package com.example.demo.controller;

import com.example.demo.exception.CustomException;
import com.example.demo.model.ProductDto;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(MainController.class)
class MainControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @InjectMocks
    private MainController mainController;


    @Test
    void testGetAllProductsSuccess() throws Exception {
        List<ProductDto> productDtoList = List.of(new ProductDto(), new ProductDto());
        when(productService.getAllProducts(anyString())).thenReturn(productDtoList);
/*
approach one
 */
        this.mockMvc.perform(get("/show_products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAllProductsFailure() throws Exception {
        when(productService.getAllProducts(anyString())).thenThrow(new CustomException("No products found", HttpStatus.NOT_FOUND));

        this.mockMvc.perform(get("/show_products"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetProductByIdSuccess() {
        String productId = "123";
        ProductDto productDto = new ProductDto();
        when(productService.getProductById(productId)).thenReturn(productDto);

        ResponseEntity<Object> responseEntity = mainController.products(productId, "");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productDto, responseEntity.getBody());
    }

    @Test
    void testGetProductByIdFailure() {
        String productId = "123";
        when(productService.getProductById(productId)).thenThrow(new CustomException("Product with id not found", HttpStatus.NOT_FOUND));
        try {
            mainController.products(productId, "");
        } catch (CustomException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }

    @Test
    void testAddProductSuccess() {
        ProductDto productDto = new ProductDto();
        when(productService.saveProduct(productDto, false)).thenReturn(productDto);

        ResponseEntity<ProductDto> responseEntity = mainController.addProduct(productDto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(productDto, responseEntity.getBody());
    }

    @Test
    void testUpdateProductSuccess() {
        ProductDto productDto = new ProductDto();
        when(productService.updateProduct("", new ProductDto())).thenReturn(productDto);
        ResponseEntity<ProductDto> responseEntity = mainController.updateProduct(productDto, "123");

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        /*
        approach 2
        this.mockMvc.perform(put("/update/123"))
                .andExpect(status().is2xxSuccessful());
         */
    }
}