package com.wms.service;

import com.wms.dto.request.InventoryRequest;
import com.wms.dto.response.InventoryResponse;
import com.wms.entity.Inventory;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.InventoryRepository;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;
    private Location location;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        product = Product.builder().sku("P-1").name("Prod").minStockLevel(5).build();
        product.setId(1L);
        location = Location.builder().code("L-1").description("loc").build();
        location.setId(1L);

        inventory = Inventory.builder()
                .product(product)
                .location(location)
                .quantity(10)
                .reservedQuantity(0)
                .build();
        inventory.setId(1L);
    }

    @Test
    void createInventory_Success() {
        InventoryRequest req = InventoryRequest.builder()
                .productId(1L)
                .locationId(1L)
                .quantity(10)
                .reservedQuantity(0)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(inventoryRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryResponse resp = inventoryService.createInventory(req);

        assertNotNull(resp);
        assertEquals(10, resp.getQuantity());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void createInventory_Duplicate_Throws() {
        InventoryRequest req = InventoryRequest.builder()
                .productId(1L)
                .locationId(1L)
                .quantity(10)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(inventoryRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.createInventory(req));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void adjustInventory_NegativeResult_Throws() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.adjustInventory(1L, -20));
    }

    @Test
    void getInventoryByProduct_ProductNotFound_Throws() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryByProduct(2L));
    }
}

