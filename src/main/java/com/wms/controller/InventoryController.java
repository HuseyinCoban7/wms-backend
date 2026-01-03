package com.wms.controller;

import com.wms.dto.request.InventoryRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.InventoryResponse;
import com.wms.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.MDC;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        List<InventoryResponse> response = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProduct(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(inventoryService.getInventoryByLocation(locationId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createOrUpdateInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createOrUpdateInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PatchMapping("/{id}/adjust")
    public ResponseEntity<InventoryResponse> adjustInventory(
            @PathVariable Long id,
            @RequestParam Integer delta) {
        return ResponseEntity.ok(inventoryService.adjustInventory(id, delta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}
