package com.wms.controller;

import com.wms.dto.request.SupplierRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.SupplierResponse;
import com.wms.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @Operation(summary = "Create supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.ok(ApiResponse.<SupplierResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable Long id) {
        SupplierResponse response = supplierService.getSupplier(id);
        return ResponseEntity.ok(ApiResponse.<SupplierResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all suppliers")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<SupplierResponse> response = supplierService.getAllSuppliers(activeOnly);
        return ResponseEntity.ok(ApiResponse.<List<SupplierResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers")
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> searchSuppliers(
            @RequestParam(defaultValue = "") String search,
            Pageable pageable) {
        Page<SupplierResponse> response = supplierService.searchSuppliers(search, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<SupplierResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.<SupplierResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate supplier")
    public ResponseEntity<ApiResponse<Void>> activateSupplier(@PathVariable Long id) {
        supplierService.activateSupplier(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate supplier")
    public ResponseEntity<ApiResponse<Void>> deactivateSupplier(@PathVariable Long id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }
}