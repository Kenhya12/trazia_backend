
package com.trazia.trazia_project.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trazia.trazia_project.ProductMapperTestUtils;
import com.trazia.trazia_project.controller.ProductController;
import com.trazia.trazia_project.dto.product.ProductRequest;
import com.trazia.trazia_project.dto.product.ProductResponse;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        public void testCreateProductSuccess() throws Exception {
                mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

                ProductRequest request = ProductRequest.builder()
                                .name("Producto Test")
                                .brand("Marca Test")
                                .category(ProductCategory.MEAT) // ✅ agregar categoría válida
                                .nutriments(ProductMapperTestUtils.createSampleNutrimentsRequest())
                                .build();

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Test Product")
                                .build();

                doReturn(response).when(productService).createProduct(any(ProductRequest.class), any());

                mockMvc.perform(post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Test Product"));
        }
}