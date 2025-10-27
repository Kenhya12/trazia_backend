package com.trazia.trazia_project.exception.product;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException() {
        super("exception.product.alreadyExists");
    }

    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
