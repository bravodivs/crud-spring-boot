package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class CustomException extends RuntimeException{
    private String message;
    private HttpStatus status;
    private String id;
    public CustomException(){
        super();
    }
    public CustomException(String id){
        super();
        this.id=id;
    }
    public CustomException(String message, HttpStatus status, String id){
        super(message);
        this.message=message;
        this.status = status;
        this.id=id;
    }
}
