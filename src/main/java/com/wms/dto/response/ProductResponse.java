package com.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String sku;
    private String barcode;
    private String name;
    private String description;
    private String unit;
    private BigDecimal unitPrice;
    private Integer minStockLevel;
    private String category;
}