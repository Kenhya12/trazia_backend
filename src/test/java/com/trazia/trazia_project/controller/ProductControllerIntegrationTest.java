package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.dto.product.ProductResponse;
import com.trazia.trazia_project.service.ProductService;
import com.trazia.trazia_project.security.JwtAuthenticationFilter;
import com.trazia.trazia_project.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductController.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testGetProductByIdSuccess() throws Exception {
        Long productId = 1L;
        ProductResponse response = ProductResponse.builder()
                .id(productId)
                .name("Producto Test")
                .build();

        when(productService.getProductById(productId)).thenReturn(response);

        mockMvc.perform(get("/api/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Producto Test"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        Long productId = 99L;
        when(productService.getProductById(productId))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Product not found")));
    }

    @Test
    void testListProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(1L).name("Producto 1").build(),
                ProductResponse.builder().id(2L).name("Producto 2").build());

        when(productService.listProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(products.size()))
                .andExpect(jsonPath("$[0].name").value("Producto 1"))
                .andExpect(jsonPath("$[1].name").value("Producto 2"));
    }

    @Test
    void testCreateProductBadRequest() throws Exception {
        String requestJson = "{}"; // JSON vacío

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}