package com.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String shippingAddress;
    private Long warehouseId;
    private String warehouseName;
    private String status;
    private LocalDate orderDate;
    private LocalDate shippedDate;
}