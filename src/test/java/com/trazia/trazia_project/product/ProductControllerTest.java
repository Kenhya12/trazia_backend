package com.trazia.trazia_project.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.domain.Pageable;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trazia.trazia_project.controller.product.ProductController;
import com.trazia.trazia_project.dto.product.ProductPageResponse;
import com.trazia.trazia_project.dto.product.ProductRequest;
import com.trazia.trazia_project.dto.product.UpdateProductRequest;
import com.trazia.trazia_project.dto.product.ProductResponse;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.service.product.ProductService;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

                // Desactivar stubbing estricto para evitar problemas de argument matching
                Mockito.lenient().when(productService.getUserProducts(any(Long.class), any(Pageable.class)))
                                .thenReturn(new ProductPageResponse());
                Mockito.lenient()
                                .when(productService.searchProducts(any(String.class), any(Long.class),
                                                any(Pageable.class)))
                                .thenReturn(new ProductPageResponse());
                Mockito.lenient()
                                .when(productService.getProductsByCategory(any(ProductCategory.class), any(Long.class),
                                                any(Pageable.class)))
                                .thenReturn(new ProductPageResponse());
        }

        @Test
        public void testCreateProductSuccess() throws Exception {

                ProductRequest request = ProductRequest.builder()
                                .name("Producto Test")
                                .brand("Marca Test")
                                .category(ProductCategory.MEAT)
                                .nutriments(null)
                                .build();

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Producto Test")
                                .build();

                // Aquí usamos nullable(Long.class) para que acepte null
                doReturn(response).when(productService).createProduct(any(ProductRequest.class), nullable(Long.class));

                mockMvc.perform(post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                                .param("userId", "1")) // El controlador debe convertir esto a Long
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Producto Test"));
        }

        @Test
        public void testCreateProductBadRequest() throws Exception {

                ProductRequest invalidRequest = ProductRequest.builder()
                                .name("")
                                .build();

                mockMvc.perform(post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(invalidRequest))
                                .param("userId", "1"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testUpdateProductSuccess() throws Exception {

                UpdateProductRequest request = UpdateProductRequest.builder()
                                .name("Producto Actualizado")
                                .brand("Marca Nueva")
                                .category(ProductCategory.MEAT)
                                .build();

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Producto Actualizado")
                                .build();

                doReturn(response).when(productService).updateProduct(eq(1L), any(UpdateProductRequest.class),
                                anyLong());

                mockMvc.perform(put("/api/products/1")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                                .param("userId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Producto Actualizado"));
        }

        @Test
        public void testDeleteProductSuccess() throws Exception {

                long productId = 1L;
                long userId = 1L;

                lenient().doNothing().when(productService).softDeleteProduct(productId, userId);

                mockMvc.perform(delete("/api/products/{id}", productId)
                                .param("userId", String.valueOf(userId)))
                                .andExpect(status().isNoContent());

                verify(productService, times(1)).softDeleteProduct(productId, userId);
        }

        @Test
        public void testListProducts() throws Exception {
                // Lista de ejemplo
                List<ProductResponse> products = Arrays.asList(
                                ProductResponse.builder().id(1L).name("Prod 1").build(),
                                ProductResponse.builder().id(2L).name("Prod 2").build());

                when(productService.listProducts()).thenReturn(products);

                mockMvc.perform(get("/api/products")
                                .accept(MediaType.APPLICATION_JSON)) // mejor usar accept
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Prod 1"))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].name").value("Prod 2"));

                verify(productService, times(1)).listProducts();
        }

        @Test
        public void testGetProductByIdFound() throws Exception {

                ProductResponse response = ProductResponse.builder().id(1L).name("Prod 1").build();
                doReturn(response).when(productService).getProductById(1L);

                mockMvc.perform(get("/api/products/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Prod 1"));
        }

        @Test
        public void testRestoreProduct() throws Exception {
                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Producto Restaurado")
                                .build();

                when(productService.restoreProduct(anyLong(), anyLong())).thenReturn(response);

                mockMvc.perform(patch("/api/products/1/restore")
                                .param("userId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Producto Restaurado"));
        }

        @Test
        public void testHardDeleteProduct() throws Exception {
                doNothing().when(productService).hardDeleteProduct(anyLong(), anyLong());

                mockMvc.perform(delete("/api/products/1/hard")
                                .param("userId", "1"))
                                .andExpect(status().isNoContent());

                verify(productService, times(1)).hardDeleteProduct(1L, 1L);
        }

        @Test
        public void testGetProductByIdNotFound() throws Exception {
                when(productService.getProductById(999L)).thenReturn(null);

                mockMvc.perform(get("/api/products/999"))
                                .andExpect(status().isNotFound());
        }
}