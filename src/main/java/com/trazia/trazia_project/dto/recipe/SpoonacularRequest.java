package com.trazia.trazia_project.dto.recipe;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpoonacularRequest {
    private List<String> ingredientList;
}