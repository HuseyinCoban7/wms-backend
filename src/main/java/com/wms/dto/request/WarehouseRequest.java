package com.wms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String address;
    private String city;
    private String country;
}