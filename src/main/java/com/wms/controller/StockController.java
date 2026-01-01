package com.wms.controller;

import com.wms.dto.request.StockAdjustmentRequest;
import com.wms.dto.request.StockTransferRequest;
import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.InventoryResponse;
import com.wms.service.StockService;
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
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Stock", description = "Stock management endpoints")
public class StockController {

    private final StockService stockService;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer stock between locations")
    public ResponseEntity<ApiResponse<Void>> transferStock(@Valid @RequestBody StockTransferRequest request) {
        stockService.transferStock(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust stock quantity")
    public ResponseEntity<ApiResponse<Void>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        stockService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/inventory")
    @Operation(summary = "Get inventory")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long productId) {
        List<InventoryResponse> response = stockService.getInventory(warehouseId, productId);
        return ResponseEntity.ok(ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}