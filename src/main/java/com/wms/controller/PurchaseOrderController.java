package com.wms.controller;

import com.wms.dto.request.PurchaseOrderItemRequest;
import com.wms.dto.request.PurchaseOrderRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.PurchaseOrderResponse;
import com.wms.service.PurchaseOrderService;
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
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Purchase Orders", description = "Purchase order management endpoints")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @Operation(summary = "Create purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.ok(ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to purchase order")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderItemRequest request) {
        purchaseOrderService.addItem(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve purchase order")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id) {
        purchaseOrderService.approvePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all purchase orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getAllPurchaseOrders() {
        List<PurchaseOrderResponse> response = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.<List<PurchaseOrderResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "Receive purchase order")
    public ResponseEntity<ApiResponse<Void>> receive(@PathVariable Long id) {
        purchaseOrderService.receivePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.<PurchaseOrderResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}