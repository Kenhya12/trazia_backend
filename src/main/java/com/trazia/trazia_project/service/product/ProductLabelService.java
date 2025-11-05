package com.trazia.trazia_project.service.product;

import com.trazia.trazia_project.dto.product.LabelPrintDTO;

import java.util.List;

public interface ProductLabelService {
    
    List<LabelPrintDTO> findAllByCurrentUser();
    LabelPrintDTO findById(String id);
    LabelPrintDTO create(LabelPrintDTO labelDTO);
    LabelPrintDTO update(String id, LabelPrintDTO labelDTO);
    void delete(String id);
    byte[] generatePdf(String id);
}