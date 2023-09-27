package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<?> notFoundException(ProductNotFoundException ex){
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

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> annotationFailure(Exception ex){
        return new ResponseEntity<>("Validation error: "+ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
