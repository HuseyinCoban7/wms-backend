package com.wms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseOrderRequest {

    @NotNull
    private Long supplierId;

    @NotNull
    private Long warehouseId;

    // Frontend'in gönderdiği alan
    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;
}