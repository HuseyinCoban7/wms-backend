package com.wms.service;

import com.wms.dto.request.LocationRequest;
import com.wms.dto.response.LocationResponse;
import com.wms.entity.Location;
import com.wms.entity.Warehouse;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.LocationRepository;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private LocationService locationService;

    private Warehouse warehouse;
    private Location location;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder().code("W-1").name("WH").build();
        warehouse.setId(1L);
        location = Location.builder().code("L-1").description("desc").warehouse(warehouse).build();
        location.setId(1L);
    }

    @Test
    void createLocation_Success() {
        LocationRequest req = LocationRequest.builder()
                .warehouseId(1L)
                .code("L-1")
                .description("desc")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationRepository.existsByWarehouseIdAndCode(1L, "L-1")).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationResponse resp = locationService.createLocation(req);

        assertNotNull(resp);
        assertEquals("L-1", resp.getCode());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void createLocation_WarehouseNotFound_Throws() {
        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        LocationRequest req = LocationRequest.builder().warehouseId(2L).code("X").build();
        assertThrows(ResourceNotFoundException.class, () -> locationService.createLocation(req));
    }

    @Test
    void createLocation_DuplicateCode_Throws() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationRepository.existsByWarehouseIdAndCode(1L, "L-1")).thenReturn(true);

        LocationRequest req = LocationRequest.builder().warehouseId(1L).code("L-1").build();
        assertThrows(BusinessRuleException.class, () -> locationService.createLocation(req));
    }
}

