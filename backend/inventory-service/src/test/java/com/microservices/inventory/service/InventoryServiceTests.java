package com.microservices.inventory.service;

import com.microservices.common.StockCheckResponse;
import com.microservices.inventory.config.RabbitMQConfig;
import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.dao.OptimisticLockingFailureException;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class
InventoryServiceTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field exchangeNameField = InventoryService.class.getDeclaredField("exchangeName");
        exchangeNameField.setAccessible(true);
        exchangeNameField.set(inventoryService, "stock.check.exchange");
    }

    @Test
    void testIsInStock_whenInStock() {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(inventory));

        boolean inStock = inventoryService.isInStock("1234567890", 5);

        assertTrue(inStock);
        verify(inventoryRepository, times(1)).findByIsbn("1234567890");
    }

    @Test
    void testIsInStock_whenOutOfStock() {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(inventory));

        boolean inStock = inventoryService.isInStock("1234567890", 15);

        assertFalse(inStock);
        verify(inventoryRepository, times(1)).findByIsbn("1234567890");
    }

    @Test
    void testIsInStock_whenNotFound() {
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        boolean inStock = inventoryService.isInStock("1234567890", 5);

        assertFalse(inStock);
        verify(inventoryRepository, times(1)).findByIsbn("1234567890");
    }

    @Test
    void testReduceStock() {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(inventory));

        inventoryService.reduceStock("1234567890", 5);

        assertEquals(5, inventory.getQuantity());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testReduceStock_whenNotEnoughStock() {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(inventory));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStock("1234567890", 15);
        });

        assertEquals("Not enough stock for ISBN: 1234567890", exception.getMessage());
        verify(inventoryRepository, never()).save(inventory);
    }

    @Test
    void testReduceStock_whenNotFound() {
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reduceStock("1234567890", 5);
        });

        assertEquals("Inventory not found for ISBN: 1234567890", exception.getMessage());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testReceiveStockCheckRequest() {
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(new Inventory(null, "1234567890", 10)));

        inventoryService.receiveStockCheckRequest("1234567890");

        ArgumentCaptor<StockCheckResponse> responseCaptor = ArgumentCaptor.forClass(StockCheckResponse.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("stock.check.exchange"), eq(RabbitMQConfig.RESPONSE_ROUTING_KEY), responseCaptor.capture());

        StockCheckResponse capturedResponse = responseCaptor.getValue();
        assertEquals("1234567890", capturedResponse.getIsbn());
        assertTrue(capturedResponse.isInStock());
    }

    @Test
    void testUpdateStock_whenInventoryExists() {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.of(inventory));
        doThrow(OptimisticLockingFailureException.class)
                .doAnswer(invocation -> {
                    Inventory inv = invocation.getArgument(0);
                    inv.setQuantity(11);
                    return inv;
                })
                .when(inventoryRepository).save(any(Inventory.class));

        inventoryService.updateStock("1234567890");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(2)).save(inventoryCaptor.capture());

        assertEquals(11, inventoryCaptor.getAllValues().get(1).getQuantity());
    }

    @Test
    void testUpdateStock_whenInventoryDoesNotExist() {
        when(inventoryRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        Inventory newInventory = new Inventory(null, "1234567890", 1);
        doThrow(OptimisticLockingFailureException.class)
                .doAnswer(invocation -> invocation.getArgument(0))
                .when(inventoryRepository).save(any(Inventory.class));

        inventoryService.updateStock("1234567890");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(2)).save(inventoryCaptor.capture());

        assertEquals("1234567890", inventoryCaptor.getAllValues().get(1).getIsbn());
        assertEquals(1, inventoryCaptor.getAllValues().get(1).getQuantity());
    }
}
