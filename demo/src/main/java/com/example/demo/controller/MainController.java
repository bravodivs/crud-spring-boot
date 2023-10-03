package com.example.demo.controller;


import com.example.demo.model.Product;
import com.example.demo.service.ProductsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;

@RestController
public class MainController {
    @Autowired
    private ProductsService productsService;

//    private  final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = {"/show_products", "/show_products/{id}"})
    public ResponseEntity<Object> products(@PathVariable(required = false) String id, @RequestParam(defaultValue = "") String searchKey) {
        if (id == null || id.isBlank() || id.isEmpty()) {
//            logger.info("Products list displayed");
            return new ResponseEntity<>(productsService.getAllProducts(searchKey), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(productsService.getProductById(id), HttpStatus.OK);
        }
    }

    //    @Valid to validate the data
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> addProduct(
//            @Validated({Product.ValidationGroupTwo.class})
            @Valid @RequestBody Product prodObj) {
        return new ResponseEntity<>(productsService.saveProduct(prodObj, false), HttpStatus.CREATED);
    }

    @PostMapping(value = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Product>> addAllProducts(@RequestBody List<Product> prods) {
        return new ResponseEntity<>(productsService.saveAllProducts(prods), HttpStatus.CREATED);
    }


    @PutMapping(value = "/update/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable String id) {
//        logger.info("Product updated successfully");
        return new ResponseEntity<>(productsService.updateProduct(id, product), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
//        TODO: check if deleted then only log and return
//        logger.info("Product deleted successfully");
//        return new ResponseEntity<>(productsService.deleteProduct(id), HttpStatus.ACCEPTED);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/delete_all")
    public ResponseEntity<Void> deleteAllProducts(){
        productsService.deleteAllProducts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


//    @GetMapping("/check")
//    public ResponseEntity<Product> check_exception() {
//        throw new CustomException("checking the error", HttpStatus.ALREADY_REPORTED);
//    }

    @RequestMapping("/bulkExport")
    public ResponseEntity<String> exportFile(HttpServletResponse response, @RequestParam String type) throws IOException {
//        TODO: make another class in service that directs to required method after chking if list is empty
        if (type.equals("csv")) {
            productsService.exportCsv(response);
            return new ResponseEntity<>("File generated", HttpStatus.OK);
        } else if (type.equals("json")) {
            String res = productsService.exportJson(response);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        else if(type.equals("pdf")){
            productsService.exportPdf(response);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else if (type.equals("xml")){
            productsService.exportXml(response);
            return  new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("File type not valid", HttpStatus.BAD_REQUEST);
    }

//    REVIEW COMMENTS
//    : change from conflict to generic exception. use apt https status codes
//    : use responseEntity for returning response and errors.
//    : refactor the model(change name) and controller(move update code to service)
//    : creating a new errorHandler that should handle every specific and other unavoidable errors in that class
//    : on adding, return only added json with code 201.
//    : different handlers for adding, adding all;
//    : change from requestParam to pathVariable.
//    : make id autogenerated. make it String for UUID. <-then cant use built-in methods like findbyid etc
//    : add contextual path through properties, such as /api/products/view etc
//

//    NEW TASKS
//    : data validation of json responses(can be done using annotation)
//    : ->handle annotation failure error.
//    : Logging/logs concepts.

//    : add /search based on name, desc
//    : JUnit testcases

//    26/9/23 comments
//    : use annotation for date created and modified. dont do it manually
//    : instead of service, make controller return response entity. service returns only data.
//    : for show products also, use path variable. use request param for only searching etc.
//    : able to add null values. change that. Validation, NotNull, etc.
}

//  27/09/23
// :  learn-> spring active profiles
//  : learn-> queueing mechanism-rabitMQ, r kafka. <- decoupling, data reliablity.
//  : use command line arguments <- spring boot <- connection done but data not accessible
//  : add a list of strings and try to validate if each value is not empty.
//  : on updating a product, createdAt becoming null. Fix it.
//  : on validating, id required so skip its validation.

// 28/09/2023
//  : createdAt shud be non editable <- annotation
// : data validation of fields.
//  : for valdation failure, return a json response of errors. bind all the errors together and print
//  : more detailed flow of logs. more coverage in exceptions.

//29/9/2023
//: add validation for addAll, update(manually), for list of products
//TODO: apt logging. more detailed.
//TODO: test cases(2-contr, all-service), follow name stds, test case stds, package struct
//TODO: resolve sonarLint issues
//TODO: have DTO apart from DAO. use annotations in DTO.
//: delete unused classes after checking.

//WEEK 2 TASKS
//TODO: {Add bulk creation feature for products -
//      create a route which accepts list of products in a form of CSV file and add it to DB after validation.
//      ->/bulkImport
//      ->take in a csv file only. If other filetype then throw error.
//      ->A csv file will have head as column names, and each row as data.
//      ->Object mapper will check the data type of each cell while converting to json
//      ->After that validate data such name not empty, price not negative.
//      ->finally, using /addAll, save to database. Actually the saveAll method too ,checks for validations}
//TODO: {Add export feature for product detail -
//      add a route which accepts a file format (CSV, PDF, JSON & XML), and downloads an export file of all the products in the requested format.
//      ->/bulkExport?fileType=given
//      ->write logic for in this order csv->json->pdf->xml
//      ->mappers are there for all types. Jackson libs probably.
//      ->If product list empty then dont create file. Just print as no product in list.
//      ->}

//TODO: util class in a util package?? for what but??