package com.trazia.trazia_project.entity;

public enum ProductCategory {
    DAIRY("Dairy Products"),
    MEAT("Meat & Poultry"),
    VEGETABLES("Vegetables"),
    FRUITS("Fruits"),
    BAKERY("Bakery & Grains"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    SUPPLEMENTS("Supplements"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

