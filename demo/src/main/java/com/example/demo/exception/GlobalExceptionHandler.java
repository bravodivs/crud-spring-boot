package com.example.demo.exception;

import com.example.demo.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(MainController.class);
    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<?> notFoundException(ProductNotFoundException ex){
        logger.error("Not found product with id "+ex.getId());
        return new ResponseEntity<>("Product not found with id "+ex.getId(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ProductAlreadyExistsException.class)
    public ResponseEntity<?> alreadyExistsException(ProductAlreadyExistsException px){
        return new ResponseEntity<>("Product with id "+px.getId()+" already exists", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<?> customException(CustomException cx){
        HashMap<String , String> map = new HashMap<>();
        map.put("Message",cx.getMessage());
        map.put("Status", String.valueOf(cx.getStatus()));
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidArg(MethodArgumentNotValidException mx){
        HashMap<String, String> map = new HashMap<>();
        map.put("Message", Arrays.toString(mx.getDetailMessageArguments()));
        map.put("Status", mx.getStatusCode().toString());
        return new ResponseEntity<>(mx.getDetailMessageArguments(), mx.getStatusCode());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> annotationFailure(Exception ex){
        HashMap<String, String> map = new HashMap<>();
        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.BAD_REQUEST.toString());
        map.put("Stack trace", Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
