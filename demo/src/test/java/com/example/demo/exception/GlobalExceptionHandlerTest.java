package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;



import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @Mock
    private Logger logger;
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @BeforeEach
    void setUp() {
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
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
        BindingResult mockBindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");

        // Stub behavior for the mock objects
        when(mockException.getBindingResult()).thenReturn(mockBindingResult);
        when(mockBindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Object> responseEntity = globalExceptionHandler.invalidArg(mockException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("error message", ((Map<?, ?>) responseEntity.getBody()).get("fieldName"));
    }

    @Test
    void otherFailures_shouldHandleOtherExceptions() {
        Exception exception = new Exception("Test exception message");

        ResponseEntity<Object> responseEntity = globalExceptionHandler.otherFailures(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}