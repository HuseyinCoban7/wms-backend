package com.wms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderRequest {

    @NotBlank
    private String customerName;

    private String shippingAddress;

    @NotNull
    private Long warehouseId;

    private LocalDate orderDate; // Frontend'den gelen alan
}