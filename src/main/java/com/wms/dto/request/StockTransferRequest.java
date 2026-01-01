package com.wms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockTransferRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long fromLocationId;

    @NotNull
    private Long toLocationId;

    @NotNull
    @Positive
    private Integer quantity;

    private String reason;
}