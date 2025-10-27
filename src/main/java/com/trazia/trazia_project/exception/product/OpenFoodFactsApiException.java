package com.trazia.trazia_project.exception.product;

public class OpenFoodFactsApiException extends RuntimeException {
    public OpenFoodFactsApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenFoodFactsApiException(String message) {
        super(message);
    }
}

