package com.wms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long locationId;

    @NotNull
    @Positive
    private Integer quantity;
}