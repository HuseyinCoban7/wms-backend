package com.wms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequest {
    @NotBlank
    private String code;

    private String description;
    private String aisle;
    private String rack;
    private String bin;

    @NotNull
    private Long warehouseId;
}
