import com.trazia.trazia_project.exceptions.DuplicateProductException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
    }

    @Test
    public void testCreateProductSuccess() {
        when(productRepository.existsByName(sampleProduct.getName())).thenReturn(false);
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);

        Product result = productService.createProduct(sampleProduct);

        assertNotNull(result);
        assertEquals(sampleProduct.getName(), result.getName());
    }

    @Test
    public void testCreateProductDuplicateThrows() {
        when(productRepository.existsByName(sampleProduct.getName())).thenReturn(true);

        assertThrows(DuplicateProductException.class, () -> {
            productService.createProduct(sampleProduct);
        });
    }

    @Test
    public void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getProductById(1L);

        assertEquals(sampleProduct.getId(), result.getId());
    }

    @Test
    public void testGetProductByIdNotFoundThrows() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(2L);
        });
    }

    // Agrega más tests para update, delete, propiedad, etc.
}
