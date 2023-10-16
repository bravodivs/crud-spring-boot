package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductDto;
import com.example.demo.repository.ProductRepository;

import com.example.demo.util.ProductUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    private ProductDto sampleProductDto;

    @BeforeEach
    void setUp() {
        sampleProductDto = new ProductDto("Sample Product",
                "Sample Description",
                13,
                23.0,
                List.of("image1", "image2"));
    }

    @Test
    void whenSearchKey_thenGetProduct() {
        String searchKey = "2";
        List<String> imageList = new ArrayList<>();
        imageList.add("image1");
        List<Product> productList = List.of(
                ProductUtils.productDtoToDao(new ProductDto("Product 1", "Description 1", 10, 100.0, imageList)),
                ProductUtils.productDtoToDao(new ProductDto("Product 2", "Description 2", 10, 100.0, imageList))
        );

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey))
                .thenReturn(Collections.singletonList(productList.get(0)));

        List<ProductDto> result = productService.getAllProducts(searchKey);

        verify(productRepository, times(1)).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
        verifyNoMoreInteractions(productRepository);
        assertNotNull(result, "Result should not be null");

        assertEquals(1, result.size(), "Expected 1 products in the result");
    }

    @Test
    void whenInvalidSearchKey_thenNoProductFound() {
        String searchKey = "nonexistent";
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomException.class, () -> productService.getAllProducts(searchKey));
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void whenNoSearchKey_thenReturnAll() {
        String searchKey = ""; // or it can be null too.
        when(productRepository.findAll()).thenReturn(Collections.singletonList(ProductUtils.productDtoToDao(sampleProductDto)));

        List<ProductDto> result = productService.getAllProducts(searchKey);
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
        assertNotNull(result, "Result should not be null");

        assertEquals(1, result.size(), "Expected 1 products in the result");
    }

    @Test
    void whenInvalidProductDto_thenThrowException() {
        sampleProductDto.setName(null);
        assertThrows(IllegalArgumentException.class, () -> productService.saveProduct(sampleProductDto, false));
    }

    @Test
    void testGetProductByIdWithValidId() {
        String validId = "1";
        Product product = new Product();
        product.setId(validId);

        when(productRepository.findById(validId)).thenReturn(Optional.of(product));

        ProductDto result = productService.getProductById(validId);

        assertNotNull(result);
        assertEquals(validId, result.getId());
    }

    @Test
    void testGetProductByIdWithNullId() {
        assertThrows(CustomException.class, () -> productService.getProductById(null));
    }

    @Test
    void testGetProductByIdWithNonExistingId() {
        String nonExistingId = "999";

        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> productService.getProductById(nonExistingId));
    }

    @Test
    void testSaveProductWithValidProductDto() {
        ProductDto productDto = new ProductDto();
//        productDto.setId("1");

        when(productRepository.save(any())).thenReturn(new Product());
        ProductDto result = productService.saveProduct(productDto, false);

        assertNotNull(result);
        assertEquals(productDto.getId(), result.getId());
    }

    @Test
    void testSaveProductWithDuplicateId() {
        ProductDto productDto = new ProductDto();
        productDto.setId("1");

//        when(productRepository.save(any())).thenReturn(new Product());
        when(productRepository.findById(productDto.getId())).thenReturn(Optional.of(new Product()));

        assertThrows(CustomException.class, () -> productService.saveProduct(productDto, false));
    }
}

