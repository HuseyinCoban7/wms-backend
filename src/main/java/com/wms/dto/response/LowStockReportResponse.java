package com.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockReportResponse {
    private Long productId;
    private String productName;
    private String productSku;
    private Long locationId;
    private String locationCode;
    private Long warehouseId;
    private String warehouseName;
    private Integer currentQuantity;
    private Integer minStockLevel;
    private Integer deficit;
}