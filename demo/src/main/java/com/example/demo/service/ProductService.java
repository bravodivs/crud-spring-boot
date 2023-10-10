package com.example.demo.service;

import com.example.demo.exception.CustomException;
import com.example.demo.model.Product;
import com.example.demo.model.ProductDto;
import com.example.demo.repository.ProductRepository;

import com.example.demo.util.ProductUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductImportService productImportService;
    @Autowired
    ProductExportService productExportService;

    public List<ProductDto> getAllProducts(String searchKey) {
        if (searchKey==null || searchKey.isBlank()) {
            List<Product> productList = productRepository.findAll();
            List<ProductDto> productDtoList = productList.stream()
                    .map(ProductUtils::productDaoToDto)
                    .toList();
            logger.info("List of products with size {} returned", productDtoList.size());
            return productDtoList;
        } else {
            logger.info("Search key {}", searchKey);
            List<Product> searchedProducts = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
            logger.info("Found products list of size {}", searchedProducts.size());
            return searchedProducts.stream()
                    .map(ProductUtils::productDaoToDto)
                    .toList();
        }
    }

    public ProductDto getProductById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new CustomException("Null id passed", HttpStatus.NOT_FOUND);
        }
        Product product = productRepository.findById(id).orElseThrow(() -> {
            logger.error("Product not found with id {}", id);
            return new CustomException(String.format("Product with id %s not found.", id), HttpStatus.NOT_FOUND);
        });
        return ProductUtils.productDaoToDto(product);
    }

    public ProductDto saveProduct(ProductDto productDto, boolean update) {
        Product product = ProductUtils.productDtoToDao(productDto);

        if (!update && product.getId() != null && getProductById(product.getId()) != null) {
            logger.error("Tried to add product wih duplicate id");
            throw new CustomException(String.format("Product with id %s already exists", product.getId()), HttpStatus.CONFLICT);
        }
        productRepository.save(product);
        logger.info("Product saved");
        return getProductById(product.getId());
    }

    public List<ProductDto> saveAllProducts(List<ProductDto> productDtoList) {
        productDtoList.forEach(product -> {
            if (Boolean.FALSE.equals(ProductUtils.validateProduct(product))) {
                logger.error("Invalid arguments provided while saving a list of products");
                throw new CustomException("Invalid arguments while saving//updating", HttpStatus.BAD_REQUEST);
            }
        });
        List<Product> productList = productDtoList.stream()
                .map(ProductUtils::productDtoToDao)
                .toList();
        productRepository.saveAll(productList);
        logger.info("Product list saved");
        return getAllProducts("");
    }

    public void deleteProduct(String id) {
        getProductById(id);
        productRepository.deleteById(id);
        logger.info("Product with id {} deleted", id);
    }

    public ProductDto updateProduct(String id, ProductDto productDto) {
        ProductDto oldProductDto = getProductById(id);

        if (productDto.getId() == null) {
            productDto.setId(oldProductDto.getId());
        }

        productDto.setName(Optional.ofNullable(productDto.getName()).orElse(oldProductDto.getName()));
        productDto.setDescription(Optional.ofNullable(productDto.getDescription()).orElse(oldProductDto.getDescription()));
        productDto.setQuantity(Optional.ofNullable(productDto.getQuantity()).orElse(oldProductDto.getQuantity()));
        productDto.setPrice(Optional.ofNullable(productDto.getPrice()).orElse(oldProductDto.getPrice()));
        productDto.setImages(Optional.ofNullable(productDto.getImages()).orElse(oldProductDto.getImages()));
        productDto.setCreatedAt(oldProductDto.getCreatedAt());

        if (Boolean.TRUE.equals(ProductUtils.validateProduct(productDto))) {
            saveProduct(productDto, true);
        } else {
            logger.error("Invalid arguments while updating a product!");
            throw new CustomException("Invalid arguments", HttpStatus.BAD_REQUEST);
        }

        logger.info("Service updateProduct: Product with id {} updated", id);
        return getProductById(id);
    }
    public ResponseEntity<Object> bulkExport(String fileType) {
        List<ProductDto> productDtoList = getAllProducts("");
        if (CollectionUtils.isEmpty(productDtoList)) {
            logger.error("Service bulkExport: Product list is empty. Can't export");
            throw new CustomException("No data to be exported", HttpStatus.NOT_FOUND);
        }
        return productExportService.exportHandler(productDtoList, fileType);
    }

    @Transactional
    public ResponseEntity<Object> bulkImport(MultipartFile file) {
        List<ProductDto> productDtoList = productImportService.readCsv(file);

        for (ProductDto productDto : productDtoList) {
            if (Boolean.FALSE.equals(ProductUtils.validateProduct(productDto))) {
                logger.error("Invalid file contents. Validation failed");
                throw new CustomException("File contents are invalid", HttpStatus.NOT_ACCEPTABLE);
            }
        }
        saveAllProducts(productDtoList);
        logger.info("Service bulkImport: Contents of imported file saved");
        return new ResponseEntity<>("File uploaded and saved successfully", HttpStatus.OK);
    }

}
