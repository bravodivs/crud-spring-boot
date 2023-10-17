package com.example.demo.controller;

import com.example.demo.model.ProductDto;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    private ProductService productService;

    @GetMapping(value = {"/show_products", "/show_products/{id}"})
    public ResponseEntity<Object> products(@PathVariable(required = false) String id, @RequestParam(defaultValue = "") String searchKey) {
        if (id == null || id.isBlank() || id.isEmpty()) {
            return new ResponseEntity<>(productService.getAllProducts(searchKey), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.saveProduct(productDto, false), HttpStatus.CREATED);
    }

    @PostMapping(value = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProductDto>> addAllProducts(@RequestBody List<ProductDto> productDtoList) {
        return new ResponseEntity<>(productService.saveAllProducts(productDtoList), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto, @PathVariable String id) {
        return new ResponseEntity<>(productService.updateProduct(id, productDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/bulkExport")
    public ResponseEntity<Object> exportFile(HttpServletResponse response, @RequestParam String type) {
        return productService.bulkExport(type);
    }

    @PostMapping(value = "/bulkImport")
    public ResponseEntity<Object> importFile(@RequestParam("file") MultipartFile file) {
        return productService.bulkImport(file);
    }
}