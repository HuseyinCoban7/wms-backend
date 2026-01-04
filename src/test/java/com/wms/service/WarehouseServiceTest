package com.wms.service;

import com.wms.dto.request.WarehouseRequest;
import com.wms.dto.response.WarehouseResponse;
import com.wms.entity.Warehouse;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.WarehouseRepository;
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
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder().code("W-1").name("WH").build();
        warehouse.setId(1L);
    }

    @Test
    void createWarehouse_Success() {
        WarehouseRequest req = new WarehouseRequest();
        req.setCode("W-1");
        req.setName("WH");

        when(warehouseRepository.existsByCode("W-1")).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponse resp = warehouseService.createWarehouse(req);

        assertNotNull(resp);
        assertEquals("W-1", resp.getCode());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void createWarehouse_DuplicateCode_Throws() {
        when(warehouseRepository.existsByCode("W-1")).thenReturn(true);

        WarehouseRequest req = new WarehouseRequest();
        req.setCode("W-1");
        req.setName("WH");
        assertThrows(BusinessRuleException.class, () -> warehouseService.createWarehouse(req));
    }

    @Test
    void getWarehouse_NotFound_Throws() {
        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.getWarehouse(2L));
    }
}
