package com.trazia.trazia_project.controller.product;

import com.trazia.trazia_project.dto.external.openfoodfacts.OpenFoodFactsProductDTO;
import com.trazia.trazia_project.dto.external.openfoodfacts.OpenFoodFactsSearchResultDTO;
import com.trazia.trazia_project.service.external.OpenFoodFactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/openfoodfacts")
@RequiredArgsConstructor
public class TestOpenFoodFactsController {
    
    private final OpenFoodFactsService openFoodFactsService;
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<OpenFoodFactsProductDTO> testSearchByBarcode(@PathVariable String barcode) {
        OpenFoodFactsProductDTO product = openFoodFactsService.searchByBarcode(barcode);
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/search")
    public ResponseEntity<OpenFoodFactsSearchResultDTO> testSearchByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int pageSize) {
        OpenFoodFactsSearchResultDTO result = openFoodFactsService.searchByName(query, pageSize);
        return ResponseEntity.ok(result);
    }
}


