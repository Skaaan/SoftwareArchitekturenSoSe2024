package com.microservices.inventory.service;

import com.microservices.common.StockCheckResponse;
import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InventoryServiceTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<Inventory> inventoryArgumentCaptor;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        setField(inventoryService, "exchangeName", "stock.check.exchange");
        setField(inventoryService, "stockCheckResponseRoutingKey", "stock.check.response.routing.key");
    }

    private void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testIsInStock_True() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        boolean inStock = inventoryService.isInStock("123", 5);

        assertTrue(inStock);
    }

    @Test
    void testIsInStock_False() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        boolean inStock = inventoryService.isInStock("123", 15);

        assertFalse(inStock);
    }

    @Test
    void testReduceStock_Success() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        inventoryService.reduceStock("123", 5);

        verify(inventoryRepository).save(inventoryArgumentCaptor.capture());
        Inventory savedInventory = inventoryArgumentCaptor.getValue();

        assertEquals(5, savedInventory.getQuantity());
    }

    @Test
    void testReduceStock_NotEnoughStock() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStock("123", 15);
        });

        assertEquals("Not enough stock for ISBN: 123", exception.getMessage());
    }

    @Test
    void testReduceStock_InventoryNotFound() {
        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStock("123", 5);
        });

        assertEquals("Inventory not found for ISBN: 123", exception.getMessage());
    }

    @Test
    void testReceiveStockCheckRequest() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        inventoryService.receiveStockCheckRequest("123");

        verify(rabbitTemplate).convertAndSend(eq("stock.check.exchange"), eq("stock.check.response.routing.key"), any(StockCheckResponse.class));
    }

    @Test
    void testUpdateStock_NewProduct() {
        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.empty());

        inventoryService.updateStock("123");

        verify(inventoryRepository).save(inventoryArgumentCaptor.capture());
        Inventory savedInventory = inventoryArgumentCaptor.getValue();

        assertEquals("123", savedInventory.getIsbn());
        assertEquals(1, savedInventory.getQuantity());
    }

    @Test
    void testUpdateStock_ExistingProduct() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        inventoryService.updateStock("123");

        verify(inventoryRepository).save(inventoryArgumentCaptor.capture());
        Inventory savedInventory = inventoryArgumentCaptor.getValue();

        assertEquals("123", savedInventory.getIsbn());
        assertEquals(11, savedInventory.getQuantity());
    }

    @Test
    void testHandleProductDelete_ProductExists() {
        Inventory inventory = new Inventory(1L, "123", 10);

        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.of(inventory));

        inventoryService.handleProductDelete("123");

        verify(inventoryRepository).delete(inventory);
    }

    @Test
    void testHandleProductDelete_ProductDoesNotExist() {
        when(inventoryRepository.findByIsbn("123")).thenReturn(Optional.empty());

        inventoryService.handleProductDelete("123");

        verify(inventoryRepository, never()).delete(any(Inventory.class));
    }
}
