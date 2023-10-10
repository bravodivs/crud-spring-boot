package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class GlobalExceptionHandlerTest {
    @Mock
    private Logger logger;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void customException_shouldHandleCustomException() {
        CustomException customException = new CustomException("Custom exception message", HttpStatus.BAD_REQUEST);

        ResponseEntity<Object> responseEntity = globalExceptionHandler.customException(customException);

        assertEquals(customException.getStatus(), responseEntity.getStatusCode());
        assertEquals(customException.getMessage(), ((String) ((java.util.Map) responseEntity.getBody()).get("Message")));
        verify(logger).error("Error encountered {} with message {}", customException.getStatus(), customException.getMessage());
    }

    @Test
    void invalidArg_shouldHandleMethodArgumentNotValidException() {
//        ResponseEntity<Object> responseEntity = globalExceptionHandler.invalidArg(invalidException);

//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        verify(logger).error("Invalid argument(s) exception encountered - {}", invalidException.getMessage());
    }

    @Test
    void otherFailures_shouldHandleOtherExceptions() {
        Exception exception = new Exception("Test exception message");

        ResponseEntity<Object> responseEntity = globalExceptionHandler.otherFailures(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(logger).error("Exception encountered: {}", exception.getMessage());
    }
}