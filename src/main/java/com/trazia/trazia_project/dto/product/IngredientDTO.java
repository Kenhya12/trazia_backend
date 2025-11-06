package com.trazia.trazia_project.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientDTO {
    
    @NotBlank(message = "Ingredient name is required")
    private String name;
    
    private String quantity;
    
    private Boolean isAllergen = false;

    // Constructors
    public IngredientDTO() {
    }

    public IngredientDTO(String name, String quantity, Boolean isAllergen) {
        this.name = name;
        this.quantity = quantity;
        this.isAllergen = isAllergen;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsAllergen() {
        return isAllergen;
    }

    public void setIsAllergen(Boolean isAllergen) {
        this.isAllergen = isAllergen;
    }

    @Override
    public String toString() {
        return "IngredientDTO{" +
                "name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", isAllergen=" + isAllergen +
                '}';
    }
}