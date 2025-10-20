package com.trazia.trazia_project.entity.product;

public enum ProductCategory {
    CONDIMENTS("Condiments"),
    DAIRY("Dairy Products"),
    EGGS("Eggs"),
    FROZEN_FOODS("Frozen Foods"),
    GRAINS("Grains & Cereals"),
    HERBS("Herbs"),
    ICE_CREAM("Ice Cream"),
    LEGUMES("Legumes"),
    MEAT("Meat & Poultry"),
    PASTA("Pasta & Noodles"),
    PROCESSED_MEAT("Processed Meat"),
    VEGETABLES("Vegetables"),
    FRUITS("Fruits"),
    BAKERY("Bakery & Grains"),
    BEVERAGES("Beverages"),
    SEAFOOD("Seafood"),
    SNACKS("Snacks"),
    SPICES("Spices"),
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

