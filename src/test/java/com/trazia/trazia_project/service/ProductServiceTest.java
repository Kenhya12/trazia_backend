package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.ProductRequest;
import com.trazia.trazia_project.dto.product.ProductResponse;
import com.trazia.trazia_project.dto.product.UpdateProductRequest;
import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.dto.product.NutrimentsRequest;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.exception.DuplicateProductException;
import com.trazia.trazia_project.exception.ProductNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.model.NutrimentsDTO;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

        @InjectMocks
        private ProductService productService;

        @Mock
        private ProductRepository productRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private ProductMapper productMapper;

        @Mock
        private MessageSource messageSource;

        @Mock
        private ImageStorageService imageStorageService;

        private User user;
        private Product product;
        private ProductRequest productRequest;
        private ProductResponse productResponse;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);

                // Usuario de prueba
                user = User.builder()
                                .id(1L)
                                .build();

                // Nutrientes del producto (entidad)
                ProductNutriments nutrimentsEntity = ProductNutriments.builder()
                                .calories(BigDecimal.valueOf(100.0))
                                .protein(BigDecimal.valueOf(5.0))
                                .carbohydrates(BigDecimal.valueOf(10.0))
                                .fat(BigDecimal.valueOf(2.0))
                                .saturatedFat(BigDecimal.valueOf(0.5))
                                .fiber(BigDecimal.valueOf(1.0))
                                .sugars(BigDecimal.valueOf(3.0))
                                .sodium(BigDecimal.valueOf(0.2))
                                .salt(BigDecimal.valueOf(0.5))
                                .build();

                // Producto de prueba (entidad)
                product = Product.builder()
                                .id(1L)
                                .name("Producto Test")
                                .category(ProductCategory.MEAT)
                                .user(user)
                                .nutriments(nutrimentsEntity)
                                .build();

                // NutrimentsRequest para ProductRequest
                NutrimentsRequest nutrimentsRequest = NutrimentsRequest.builder()
                                .calories(BigDecimal.valueOf(100.0))
                                .protein(BigDecimal.valueOf(5.0))
                                .carbohydrates(BigDecimal.valueOf(10.0))
                                .fat(BigDecimal.valueOf(2.0))
                                .saturatedFat(BigDecimal.valueOf(0.5))
                                .fiber(BigDecimal.valueOf(1.0))
                                .sugars(BigDecimal.valueOf(3.0))
                                .sodium(BigDecimal.valueOf(0.2))
                                .salt(BigDecimal.valueOf(0.5))
                                .build();

                // ProductRequest de prueba
                productRequest = ProductRequest.builder()
                                .name("Producto Test")
                                .category(ProductCategory.MEAT)
                                .nutriments(nutrimentsRequest)
                                .build();

                // NutrimentsDTO para test
                NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                                .calories(100.0)
                                .protein(5.0)
                                .carbohydrates(10.0)
                                .fat(2.0)
                                .saturatedFat(0.5)
                                .fiber(1.0)
                                .sugars(3.0)
                                .sodium(0.2)
                                .build();

                // ProductResponse de prueba
                productResponse = ProductResponse.builder()
                                .id(1L)
                                .name("Producto Test")
                                .nutriments(nutrimentsDTO)
                                .build();
        }

        @Test
        void createProduct_Success() {
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(productRepository.existsByUserIdAndName(1L, "Producto Test")).thenReturn(false);
                when(productMapper.toEntity(productRequest, user)).thenReturn(product);
                when(productRepository.save(product)).thenReturn(product);
                when(productMapper.toResponse(product)).thenReturn(productResponse);

                ProductResponse response = productService.createProduct(productRequest, 1L);

                assertNotNull(response);
                assertEquals("Producto Test", response.getName());
                verify(productRepository).save(product);
        }

        @Test
        void createProduct_DuplicateName_ThrowsException() {
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(productRepository.existsByUserIdAndName(1L, "Producto Test")).thenReturn(true);
                when(messageSource.getMessage(eq("product.duplicate"), any(), any()))
                                .thenReturn("Duplicate product");

                assertThrows(DuplicateProductException.class,
                                () -> productService.createProduct(productRequest, 1L));
        }

        @Test
        void getProductById_Success() {
                when(productRepository.findById(1L)).thenReturn(Optional.of(product));
                when(productMapper.toResponse(product)).thenReturn(productResponse);

                ProductResponse response = productService.getProductById(1L);

                assertNotNull(response);
                assertEquals(1L, response.getId());
                assertEquals("Producto Test", response.getName());
        }

        @Test
        void getProductById_NotFound_ThrowsException() {
                when(productRepository.findById(1L)).thenReturn(Optional.empty());
                when(messageSource.getMessage(eq("product.notFound"), any(), any()))
                                .thenReturn("Product not found");

                assertThrows(ProductNotFoundException.class,
                                () -> productService.getProductById(1L));
        }

        @Test
        void updateProduct_Success() {
                UpdateProductRequest updateRequest = UpdateProductRequest.builder()
                                .name("Nuevo Nombre")
                                .build();

                when(productRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(product));
                when(productRepository.existsByUserIdAndName(1L, "Nuevo Nombre")).thenReturn(false);
                when(productRepository.save(product)).thenReturn(product);
                when(productMapper.toResponse(product)).thenReturn(productResponse);

                ProductResponse response = productService.updateProduct(1L, updateRequest, 1L);
                assertNotNull(response);
                assertEquals("Producto Test", response.getName());
                verify(productRepository).save(product);
        }
}