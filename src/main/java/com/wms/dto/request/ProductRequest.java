package com.wms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor        // <-- Jackson için parametresiz ctor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank
    private String sku;

    private String barcode;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String unit;

    @NotNull
    @Min(0)
    private BigDecimal unitPrice;

    @NotNull
    @Min(0)
    private Integer minStockLevel;

    private String category;
}