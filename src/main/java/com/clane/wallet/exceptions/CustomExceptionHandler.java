package com.clane.wallet.exceptions;

import com.clane.wallet.responses.Response;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintValidation(ConstraintViolationException exception) {
        Response<?> response = new Response<>();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.addValidationErrors(exception.getConstraintViolations());
        response.setMessage("Validation Error");
        return buildResponseEntity(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException customException) {
        Response<?> response = new Response<>();
        response.setMessage(customException.getMessage());
        response.setStatus(customException.getStatus());
        return buildResponseEntity(response);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        Response<?> response = new Response<>();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.addValidationError(exception.getBindingResult().getAllErrors());
        response.setMessage("Validation Error");
        return buildResponseEntity(response);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception, HttpHeaders headers,
                                                     HttpStatus status, WebRequest request) {
        Response<?> response = new Response<>();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage("Invalid input for type: " + exception.getRequiredType());
        return buildResponseEntity(response);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                      HttpHeaders headers, HttpStatus status,
                                                                      WebRequest request) {
        Response<?> response = new Response<>();
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setMessage("Invalid request method " + exception.getMethod());
        return buildResponseEntity(response);
    }

    private ResponseEntity<Object> buildResponseEntity(Response<?> response) {
        return new ResponseEntity<>(response, response.getStatus());
    }
}
