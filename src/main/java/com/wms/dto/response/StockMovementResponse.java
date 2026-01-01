package com.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    private Long id;
    private String type;
    private Long productId;
    private String productName;
    private Long fromLocationId;
    private String fromLocationCode;
    private Long toLocationId;
    private String toLocationCode;
    private Integer quantity;
    private String reason;
    private LocalDateTime movementDate;
    private String referenceNumber;
}