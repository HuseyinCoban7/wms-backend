package com.wms.controller;

import com.wms.dto.request.LocationRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.LocationResponse;
import com.wms.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Locations", description = "Location management endpoints")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Create location")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.ok(ApiResponse.<LocationResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocations() {
        List<LocationResponse> response = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.<List<LocationResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get locations by warehouse")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsByWarehouse(@PathVariable Long warehouseId) {
        List<LocationResponse> response = locationService.getLocationsByWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.<List<LocationResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(@PathVariable Long id) {
        LocationResponse response = locationService.getLocation(id);
        return ResponseEntity.ok(ApiResponse.<LocationResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.updateLocation(id, request);
        return ResponseEntity.ok(ApiResponse.<LocationResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete location")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }
}