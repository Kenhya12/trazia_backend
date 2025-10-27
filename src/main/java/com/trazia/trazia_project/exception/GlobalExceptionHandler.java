package com.trazia.trazia_project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.trazia.trazia_project.dto.common.ErrorResponse;
import com.trazia.trazia_project.exception.auth.InvalidCredentialsException;
import com.trazia.trazia_project.exception.auth.UserAlreadyExistsException;
import com.trazia.trazia_project.exception.product.ProductNotFoundException;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        private ErrorResponse buildErrorResponse(Exception ex, WebRequest request, HttpStatus status, String title) {
                return new ErrorResponse(
                                LocalDateTime.now(),
                                status.value(),
                                title,
                                Optional.ofNullable(ex.getMessage()).orElse("No message"),
                                request.getDescription(false).replace("uri=", ""));
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
                        WebRequest request) {
                log.warn("User already exists error: {}", ex.getMessage());
                return new ResponseEntity<>(buildErrorResponse(ex, request, HttpStatus.CONFLICT, "User Already Exists"),
                                HttpStatus.CONFLICT);
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex,
                        WebRequest request) {
                log.warn("Product not found: {}", ex.getMessage());
                return new ResponseEntity<>(buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "Product Not Found"),
                                HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex,
                        WebRequest request) {
                log.warn("Invalid credentials: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "Invalid Credentials"),
                                HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
                log.warn("Access denied: {}", ex.getMessage());
                return new ResponseEntity<>(buildErrorResponse(ex, request, HttpStatus.FORBIDDEN, "Access Denied"),
                                HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                        WebRequest request) {
                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
                ex.getBindingResult().getGlobalErrors()
                                .forEach(error -> fieldErrors.put(error.getObjectName(), error.getDefaultMessage()));

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation Error",
                                "Invalid input data",
                                request.getDescription(false).replace("uri=", ""));
                errorResponse.setErrors(fieldErrors); // Suponiendo que ErrorResponse tenga un campo Map<String,String>
                                                      // errors
                log.warn("Validation errors: {}", fieldErrors);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
                log.error("Unexpected error occurred", ex);
                return new ResponseEntity<>(buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR,
                                "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
