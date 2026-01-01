package com.wms.controller;

import com.wms.dto.response.ApiResponse;
import com.wms.dto.response.LowStockReportResponse;
import com.wms.dto.response.StockMovementResponse;
import com.wms.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Reports", description = "Reporting endpoints")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock report")
    public ResponseEntity<ApiResponse<List<LowStockReportResponse>>> getLowStockReport() {
        List<LowStockReportResponse> response = reportService.getLowStockReport();
        return ResponseEntity.ok(ApiResponse.<List<LowStockReportResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }

    @GetMapping("/movements")
    @Operation(summary = "Get stock movements report")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getMovementReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<StockMovementResponse> response = reportService.getMovementReport(from, to);
        return ResponseEntity.ok(ApiResponse.<List<StockMovementResponse>>builder()
                .success(true)
                .data(response)
                .traceId(MDC.get("requestId"))
                .build());
    }
}