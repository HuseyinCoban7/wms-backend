package com.wms.controller;

import com.wms.dto.request.WarehouseRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.WarehouseResponse;
import com.wms.service.WarehouseService;
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
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Warehouses", description = "Warehouse management endpoints")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(summary = "Create warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.ok(ApiResponse.<WarehouseResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        List<WarehouseResponse> response = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.<List<WarehouseResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getWarehouse(id);
        return ResponseEntity.ok(ApiResponse.<WarehouseResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}