package com.microservices.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.microservices.product.config.RabbitMQConfig;
import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(productService, "exchangeName", "stock.check.exchange");

        productRequest = ProductRequest.builder()
                .isbn("1234567890")
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(8.00))
                .imageLink("http://example.com/image.jpg")
                .author("Author")
                .genre("Genre")
                .publishedYear("2023")
                .quantity(1)
                .build();

        product = Product.builder()
                .id(1L)
                .isbn("1234567890")
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(8.00))
                .imageLink("http://example.com/image.jpg")
                .author("Author")
                .genre("Genre")
                .publishedYear("2023")
                .quantity(1)
                .build();
    }

    @Test
    void testCreateProduct_NewProduct() {
        when(productRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.createProduct(productRequest);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.PRODUCT_ROUTING_KEY), eq(productRequest.getIsbn()));
    }

    @Test
    void testCreateProduct_ExistingProduct() {
        when(productRepository.findByIsbn(anyString())).thenReturn(Optional.of(product));

        int initialQuantity = product.getQuantity();

        productService.createProduct(productRequest);

        verify(productRepository, times(1)).save(product);
        assertEquals(initialQuantity + 1, product.getQuantity());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.PRODUCT_ROUTING_KEY), eq(productRequest.getIsbn()));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> products = productService.getAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.findByIsbn(anyString())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProduct("1234567890");

        verify(productRepository, times(1)).deleteById(product.getId());
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.findByIsbn(anyString())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse updatedProduct = productService.updateProduct("1234567890", productRequest);

        assertEquals(productRequest.getName(), updatedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }
}
