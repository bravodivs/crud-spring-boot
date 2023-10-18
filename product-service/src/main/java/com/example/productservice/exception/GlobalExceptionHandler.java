package com.example.productservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Object> customException(CustomException cx) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Message", cx.getMessage());
        map.put("Status", String.valueOf(cx.getStatus()));
        logger.error("Error encountered {} with message {}", cx.getStatus(), cx.getMessage());
        return new ResponseEntity<>(map, cx.getStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> invalidArg(MethodArgumentNotValidException mx) {
        Map<String, String> errors = new HashMap<>();
        mx.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        logger.error("Invalid argument(s) exception encountered - {}", mx.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> otherFailures(Exception ex) {
        HashMap<String, String> map = new HashMap<>();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        map.put("Message", ex.getMessage());
        map.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        map.put("Stack trace", sw.toString());
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
