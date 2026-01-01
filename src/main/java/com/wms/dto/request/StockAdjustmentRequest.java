package com.wms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StockAdjustmentRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long locationId;

    @NotNull
    @PositiveOrZero
    private Integer newQuantity;

    private String reason;
}