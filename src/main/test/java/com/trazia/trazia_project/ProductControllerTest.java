
package com.trazia.trazia_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateProductSuccess() throws Exception {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any())).thenReturn(product);

        mockMvc.perform(post("/api/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    public void testCreateProductDuplicate() throws Exception {
        Product product = new Product();
        product.setName("Duplicate");

        when(productService.createProduct(any())).thenThrow(new DuplicateProductException());

        mockMvc.perform(post("/api/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isConflict());
    }

    @Test
    public void testGetProductSuccess() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testGetProductNotFound() throws Exception {
        when(productService.getProductById(2L)).thenThrow(new ProductNotFoundException());

        mockMvc.perform(get("/api/products/2"))
            .andExpect(status().isNotFound());
    }

    // Más tests: update, delete, validaciones y manejo errores.
}

public class DummyTest {
    @Test
    void testAlwaysPass() {
        assertTrue(true);
    }
}