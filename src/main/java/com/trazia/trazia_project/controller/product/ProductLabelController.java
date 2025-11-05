package com.trazia.trazia_project.controller.product;

import com.trazia.trazia_project.dto.product.LabelPrintDTO;
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
        List<LabelPrintDTO> labels = productLabelService.findAllByCurrentUser();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelPrintDTO> getLabelById(@PathVariable String id) {
        LabelPrintDTO label = productLabelService.findById(id);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    public ResponseEntity<LabelPrintDTO> createLabel(@Valid @RequestBody LabelPrintDTO labelDTO) {
        LabelPrintDTO createdLabel = productLabelService.create(labelDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelPrintDTO> updateLabel(
            @PathVariable String id, 
            @Valid @RequestBody LabelPrintDTO labelDTO) {
        LabelPrintDTO updatedLabel = productLabelService.update(id, labelDTO);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable String id) {
        productLabelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateLabelPdf(@PathVariable String id) {
        byte[] pdfBytes = productLabelService.generatePdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "label-" + id + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}