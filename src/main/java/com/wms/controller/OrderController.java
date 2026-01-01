package com.wms.controller;

import com.wms.dto.request.OrderItemRequest;
import com.wms.dto.request.OrderRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.OrderResponse;
import com.wms.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to order")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemRequest request) {
        orderService.addItem(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve stock for order")
    public ResponseEntity<ApiResponse<Void>> reserve(@PathVariable Long id) {
        orderService.reserveStock(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "Ship order")
    public ResponseEntity<ApiResponse<Void>> ship(@PathVariable Long id) {
        orderService.shipOrder(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}