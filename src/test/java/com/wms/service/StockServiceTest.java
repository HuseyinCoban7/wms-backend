package com.wms.service;

import com.wms.dto.request.StockTransferRequest;
import com.wms.entity.Inventory;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.exception.InsufficientStockException;
import com.wms.repository.InventoryRepository;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import com.wms.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockService stockService;

    private Product product;
    private Location fromLocation;
    private Location toLocation;
    private Inventory fromInventory;
    private Inventory toInventory;

    @BeforeEach
    void setUp() {
        product = Product.builder().sku("PROD-001").name("Product 1").build();
        product.setId(1L);
        fromLocation = Location.builder().code("A-01-01").build();
        fromLocation.setId(1L);
        toLocation = Location.builder().code("A-01-02").build();
        toLocation.setId(2L);

        fromInventory = Inventory.builder()
                .product(product)
                .location(fromLocation)
                .quantity(100)
                .reservedQuantity(0)
                .build();
        fromInventory.setId(1L);

        toInventory = Inventory.builder()
                .product(product)
                .location(toLocation)
                .quantity(50)
                .reservedQuantity(0)
                .build();
        toInventory.setId(2L);
    }

    @Test
    void transferStock_Success() {
        StockTransferRequest request = StockTransferRequest.builder()
                .productId(1L)
                .fromLocationId(1L)
                .toLocationId(2L)
                .quantity(20)
                .reason("Transfer test")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(toLocation));
        when(inventoryRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(fromInventory));
        when(inventoryRepository.findByProductIdAndLocationId(1L, 2L)).thenReturn(Optional.of(toInventory));

        stockService.transferStock(request);

        assertEquals(80, fromInventory.getQuantity());
        assertEquals(70, toInventory.getQuantity());
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
        verify(stockMovementRepository, times(1)).save(any());
    }

    @Test
    void transferStock_InsufficientStock_ThrowsException() {
        StockTransferRequest request = StockTransferRequest.builder()
                .productId(1L)
                .fromLocationId(1L)
                .toLocationId(2L)
                .quantity(150)
                .reason("Transfer test")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(toLocation));
        when(inventoryRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(fromInventory));

        assertThrows(InsufficientStockException.class, () -> stockService.transferStock(request));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}