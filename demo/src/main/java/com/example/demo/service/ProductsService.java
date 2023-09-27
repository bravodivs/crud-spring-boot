package com.example.demo.service;

import com.example.demo.exception.ProductAlreadyExistsException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductsService {
    @Autowired
    ProductRepository products;

    public List<Product> getAllProducts(String searchKey) {
        if (searchKey.equals(""))
            return products.findAll();
        else {
            return products.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
        }
    }

    public Product getProductById(String id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product saveProduct(Product product) {
//      check if already present. Then do not add again. Though this would have just updated for that particular entry
        if (getProductById(product.getId()) != null)
            throw new ProductAlreadyExistsException(product.getId());
        products.save(product);
        return getProductById(product.getId());
    }

    public List<Product> saveAllProducts(List<Product> prodList) {
        products.saveAll(prodList);
        return getAllProducts("");
    }

    public List<Product> deleteProduct(String id) {

        Product p = getProductById(id);
        products.deleteById(id);
        return getAllProducts("");
    }

    public Product updateProduct(String id, Product product) {
        Product oldPr = getProductById(id);
        Product newPr = new Product();

//      : change when provided else keep the same
//      : throw error if product doesnt exist already
        if (newPr.getId() != null)
            newPr.setId(id);
        else newPr.setId(oldPr.getId());

        if (product.getName() != null)
            newPr.setName(product.getName());
        else newPr.setName(oldPr.getName());

        if (product.getDescription() != null)
            newPr.setDescription(product.getDescription());
        else newPr.setDescription(oldPr.getDescription());

        if (product.getQuantity() != 0)
            newPr.setQuantity(product.getQuantity());
        else newPr.setQuantity(oldPr.getQuantity());

        if (product.getPrice() != 0)
            newPr.setPrice(product.getPrice());
        else newPr.setPrice(oldPr.getPrice());

        newPr.setCreatedAt(oldPr.getCreatedAt());

        saveProduct(newPr);
        return getProductById(id);
    }
}
