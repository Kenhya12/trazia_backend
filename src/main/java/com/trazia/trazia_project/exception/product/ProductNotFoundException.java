package com.trazia.trazia_project.exception.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String barcode) {
        super("Product not found with barcode: " + barcode);
    }
}

