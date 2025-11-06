package com.trazia.trazia_project.controller.product;

import com.trazia.trazia_project.dto.product.LabelPrintDTO;
import com.trazia.trazia_project.dto.product.IngredientDTO;
import com.trazia.trazia_project.service.product.ProductLabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class ProductLabelController {

    @Autowired
    private ProductLabelService productLabelService;

    @GetMapping
    public ResponseEntity<List<LabelPrintDTO>> getAllLabels() {
        try {
            System.out.println("📋 GET all labels request");
            List<LabelPrintDTO> labels = productLabelService.findAllByCurrentUser();
            
            System.out.println("✅ Found " + labels.size() + " labels");
            for (int i = 0; i < labels.size(); i++) {
                LabelPrintDTO label = labels.get(i);
                System.out.println("  [" + (i+1) + "] " + label.getProductName() + 
                                 " | v" + label.getVersion() + 
                                 " | " + label.getStatus() +
                                 " | Ingredients: " + (label.getIngredients() != null ? label.getIngredients().size() + " items" : "NULL"));
            }
            
            return ResponseEntity.ok(labels);
        } catch (Exception e) {
            System.out.println("❌ ERROR in getAllLabels: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelPrintDTO> getLabelById(@PathVariable String id) {
        try {
            System.out.println("🔍 GET label request for ID: " + id);
            LabelPrintDTO label = productLabelService.findById(id);
            
            if (label != null) {
                System.out.println("✅ Label found:");
                System.out.println("   📦 Product: " + label.getProductName());
                System.out.println("   🔢 Version: " + label.getVersion());
                System.out.println("   📊 Status: " + label.getStatus());
                System.out.println("   🌐 Language: " + label.getLanguage());
                System.out.println("   🥗 Ingredients: " + 
                                 (label.getIngredients() != null ? label.getIngredients().size() + " items" : "NULL"));
                
                if (label.getIngredients() != null) {
                    for (int i = 0; i < label.getIngredients().size(); i++) {
                        IngredientDTO ing = label.getIngredients().get(i);
                        System.out.println("     [" + (i+1) + "] " + ing.getName() + 
                                         " (" + ing.getQuantity() + ")" + 
                                         (ing.getIsAllergen() ? " [ALÉRGENO]" : ""));
                    }
                }
            } else {
                System.out.println("❌ Label not found for ID: " + id);
            }
            
            return ResponseEntity.ok(label);
        } catch (Exception e) {
            System.out.println("❌ ERROR in getLabelById for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<LabelPrintDTO> createLabel(@Valid @RequestBody LabelPrintDTO labelDTO) {
        try {
            // DEBUGGING DETALLADO
            System.out.println("=== 🆕 LABEL CREATION REQUEST ===");
            System.out.println("📦 Product: " + labelDTO.getProductName());
            System.out.println("🔢 Version: " + labelDTO.getVersion());
            System.out.println("📊 Status: " + labelDTO.getStatus());
            System.out.println("🌐 Language: " + labelDTO.getLanguage());
            System.out.println("🇺🇳 Country: " + labelDTO.getCountryOfOrigin());
            System.out.println("🏷️ Batch: " + labelDTO.getBatchNumber());
            System.out.println("⚖️ Net Weight: " + labelDTO.getNetWeight());
            System.out.println("🏢 Company: " + labelDTO.getCompanyName());
            System.out.println("📅 Expiration: " + labelDTO.getExpirationDate());
            System.out.println("📝 Usage Instructions: " + labelDTO.getUsageInstructions());
            
            // DEBUG INGREDIENTES
            if (labelDTO.getIngredients() != null) {
                System.out.println("🥗 Ingredients count: " + labelDTO.getIngredients().size());
                for (int i = 0; i < labelDTO.getIngredients().size(); i++) {
                    IngredientDTO ing = labelDTO.getIngredients().get(i);
                    System.out.println("  [" + (i+1) + "] " + 
                                     "Name: '" + ing.getName() + "' | " +
                                     "Quantity: '" + ing.getQuantity() + "' | " +
                                     "Allergen: " + (ing.getIsAllergen() ? "✅ YES" : "❌ NO"));
                }
            } else {
                System.out.println("❌ Ingredients: NULL");
            }
            System.out.println("=================================");
            
            LabelPrintDTO createdLabel = productLabelService.create(labelDTO);
            
            System.out.println("✅ Label created successfully with ID: " + 
                             (createdLabel != null ? "RETURNED" : "NULL"));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in createLabel: " + e.getMessage());
            System.out.println("🔍 Error type: " + e.getClass().getName());
            e.printStackTrace();
            throw e; // re-lanzar para que se maneje el error normalmente
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelPrintDTO> updateLabel(
            @PathVariable String id, 
            @Valid @RequestBody LabelPrintDTO labelDTO) {
        try {
            // DEBUGGING PARA UPDATE
            System.out.println("=== ✏️ LABEL UPDATE REQUEST ===");
            System.out.println("🆔 Label ID: " + id);
            System.out.println("📦 Product: " + labelDTO.getProductName());
            System.out.println("🔢 Version: " + labelDTO.getVersion());
            System.out.println("📊 Status: " + labelDTO.getStatus());
            System.out.println("🥗 Ingredients count: " + 
                             (labelDTO.getIngredients() != null ? labelDTO.getIngredients().size() : 0));
            
            if (labelDTO.getIngredients() != null) {
                for (int i = 0; i < labelDTO.getIngredients().size(); i++) {
                    IngredientDTO ing = labelDTO.getIngredients().get(i);
                    System.out.println("  [" + (i+1) + "] " + ing.getName() + 
                                     " (" + ing.getQuantity() + ")" + 
                                     (ing.getIsAllergen() ? " [ALÉRGENO]" : ""));
                }
            }
            System.out.println("===============================");
            
            LabelPrintDTO updatedLabel = productLabelService.update(id, labelDTO);
            
            System.out.println("✅ Label updated successfully: " + id);
            
            return ResponseEntity.ok(updatedLabel);
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in updateLabel for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable String id) {
        try {
            System.out.println("🗑️ DELETE label request for ID: " + id);
            productLabelService.delete(id);
            System.out.println("✅ Label deleted successfully: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println("❌ ERROR in deleteLabel for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateLabelPdf(@PathVariable String id) {
        try {
            System.out.println("📄 PDF generation request for label ID: " + id);
            byte[] pdfBytes = productLabelService.generatePdf(id);
            
            System.out.println("✅ PDF generated successfully, size: " + 
                             (pdfBytes != null ? pdfBytes.length + " bytes" : "NULL"));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "label-" + id + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in generateLabelPdf for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}