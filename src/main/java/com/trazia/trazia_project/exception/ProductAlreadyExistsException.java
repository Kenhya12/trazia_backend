package com.trazia.trazia_project.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException() {
        super("exception.product.alreadyExists");
    }

    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
