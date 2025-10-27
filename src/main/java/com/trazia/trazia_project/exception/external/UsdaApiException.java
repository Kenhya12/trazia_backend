package com.trazia.trazia_project.exception.external;

public class UsdaApiException extends RuntimeException {

    // Constructor con mensaje
    public UsdaApiException(String message) {
        super(message);
    }

    // Constructor con mensaje y causa
    public UsdaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}