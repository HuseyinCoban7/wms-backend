package com.wms.service;

import com.wms.dto.request.LocationRequest;
import com.wms.dto.response.LocationResponse;
import com.wms.entity.Location;
import com.wms.entity.Warehouse;
import com.wms.exception.BusinessRuleException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.LocationRepository;
import com.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        if (locationRepository.existsByWarehouseIdAndCode(warehouse.getId(), request.getCode())) {
            throw new BusinessRuleException("Location with code " + request.getCode() + " already exists in this warehouse");
        }

        Location location = Location.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .aisle(request.getAisle())
                .rack(request.getRack())
                .bin(request.getBin())
                .warehouse(warehouse)
                .build();

        location = locationRepository.save(location);
        return mapToResponse(location);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll()  // Soft delete yerine tüm kayıtlar
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return mapToResponse(location);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationsByWarehouse(Long warehouseId) {
        return locationRepository.findByWarehouseId(warehouseId)  // Soft delete filtresi yok
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public LocationResponse updateLocation(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        if (!location.getCode().equals(request.getCode()) &&
                locationRepository.existsByWarehouseIdAndCode(warehouse.getId(), request.getCode())) {
            throw new BusinessRuleException("Location with code " + request.getCode() + " already exists in this warehouse");
        }

        location.setCode(request.getCode());
        location.setDescription(request.getDescription());
        location.setAisle(request.getAisle());
        location.setRack(request.getRack());
        location.setBin(request.getBin());
        location.setWarehouse(warehouse);

        location = locationRepository.save(location);
        return mapToResponse(location);
    }

    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        locationRepository.delete(location);  // Fiziksel silme
    }

    private LocationResponse mapToResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .code(location.getCode())
                .description(location.getDescription())
                .aisle(location.getAisle())
                .rack(location.getRack())
                .bin(location.getBin())
                .warehouseId(location.getWarehouse().getId())
                .warehouseName(location.getWarehouse().getName())
                .build();
    }
}