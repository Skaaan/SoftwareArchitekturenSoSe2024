package com.microservices.product.service;

import com.microservices.product.config.RabbitMQConfig;
import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        setField(productService, "exchangeName", "stock.check.exchange");
    }

    private void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testCreateProduct_NewProduct() {
        ProductRequest productRequest = new ProductRequest("123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        when(productRepository.findByIsbn("123")).thenReturn(Optional.empty());

        productService.createProduct(productRequest);

        verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();

        assertEquals("123", savedProduct.getIsbn());
        assertEquals("Book Name", savedProduct.getName());
        assertEquals("Description", savedProduct.getDescription());
        assertEquals( BigDecimal.valueOf(29.99), savedProduct.getPrice());
        assertEquals("imageLink", savedProduct.getImageLink());
        assertEquals("Author", savedProduct.getAuthor());
        assertEquals("Genre", savedProduct.getGenre());
        assertEquals("2021", savedProduct.getPublishedYear());
        assertEquals(1, savedProduct.getQuantity());
        verify(rabbitTemplate).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.PRODUCT_ROUTING_KEY), eq("123"));
    }

    @Test
    void testCreateProduct_ExistingProduct() {
        Product existingProduct = new Product(1L, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);
        ProductRequest productRequest = new ProductRequest("123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        when(productRepository.findByIsbn("123")).thenReturn(Optional.of(existingProduct));

        productService.createProduct(productRequest);

        verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();

        assertEquals(2, savedProduct.getQuantity());
        verify(rabbitTemplate).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.PRODUCT_ROUTING_KEY), eq("123"));
    }

    @Test
    void testGetAllProducts() {
        Product product = new Product(1L, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        List<ProductResponse> products = productService.getAllProducts();

        assertEquals(1, products.size());
        ProductResponse response = products.get(0);
        assertEquals("123", response.getIsbn());
    }

    @Test
    void testGetAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductResponse> products = productService.getAllProducts();

        assertTrue(products.isEmpty());
    }

    @Test
    void testDeleteProduct_ExistingProduct() {
        Product product = new Product(1L, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);

        when(productRepository.findByIsbn("123")).thenReturn(Optional.of(product));

        productService.deleteProduct("123");

        verify(productRepository).delete(product);
        verify(rabbitTemplate).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.PRODUCT_DELETE_ROUTING_KEY), eq("123"));
    }

    @Test
    void testDeleteProduct_NonExistingProduct() {
        when(productRepository.findByIsbn("123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct("123");
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testUpdateProduct() {
        Product existingProduct = new Product(1L, "123", "Book Name", "Description",
                BigDecimal.valueOf(29.99), "imageLink", "Author", "Genre", "2021", 1);
        ProductRequest productRequest = new ProductRequest("123", "New Book Name", "New Description",
                BigDecimal.valueOf(39.99), "newImageLink", "New Author", "New Genre", "2022", 2);

        when(productRepository.findByIsbn(String.valueOf(123))).thenReturn(Optional.of(existingProduct));

        ProductResponse updatedProduct = productService.updateProduct("123", productRequest);

        assertEquals("New Book Name", updatedProduct.getName());
        verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();

        assertEquals("New Book Name", savedProduct.getName());
        assertEquals(2, savedProduct.getQuantity());
    }

    @Test
    void testUpdateProduct_NonExistingProduct() {
        ProductRequest productRequest = new ProductRequest("123", "New Book Name", "New Description",
                BigDecimal.valueOf(39.99), "newImageLink", "New Author", "New Genre", "2022", 2);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct("1", productRequest);
        });

        assertEquals("Product not found", exception.getMessage());
    }

}