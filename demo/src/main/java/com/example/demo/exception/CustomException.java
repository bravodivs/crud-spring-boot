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
    public CustomException(String message){
        super();
        this.message=message;
        this.status=HttpStatus.CONFLICT;
    }
    public CustomException(String message, HttpStatus status){
        super(message);
        this.message=message;
        this.status = status;
    }
}
