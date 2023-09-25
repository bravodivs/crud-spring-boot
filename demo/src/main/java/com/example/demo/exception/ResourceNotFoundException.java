package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{
    private String message;
    private HttpStatus status;
    public ResourceNotFoundException(){}
    public ResourceNotFoundException(String message, HttpStatus status){
        super(message);
        this.message=message;
        this.status = status;
    }
}
