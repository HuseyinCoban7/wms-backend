package com.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String code;
    private String description;
    private String aisle;
    private String rack;
    private String bin;
    private Long warehouseId;
    private String warehouseName;
}