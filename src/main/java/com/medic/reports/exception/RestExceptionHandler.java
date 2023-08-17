package com.medic.reports.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Error handlePatientNotFoundException(PatientNotFoundException patientNotFoundException, WebRequest request) {
        return new Error(HttpStatus.NOT_FOUND, patientNotFoundException.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public Error handleServiceUnavailableException(ServiceUnavailableException serviceUnavailableException, WebRequest request) {
        return new Error(HttpStatus.SERVICE_UNAVAILABLE, serviceUnavailableException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> customValidationErrorHandling(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

