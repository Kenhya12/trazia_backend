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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex,
                        WebRequest request) {
                log.warn("User already exists error: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "User Already Exists",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleProductNotFoundException(
                        ProductNotFoundException ex,
                        WebRequest request) {
                log.warn("Product not found: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                "Product Not Found",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
                        InvalidCredentialsException ex,
                        WebRequest request) {
                log.warn("Invalid credentials: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid Credentials",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex,
                        WebRequest request) {
                log.warn("Access denied: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.FORBIDDEN.value(),
                                "Access Denied",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex,
                        WebRequest request) {
                log.error("Unexpected error occurred", ex);

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
