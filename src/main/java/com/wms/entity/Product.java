package com.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String sku;

    @Size(max = 100)
    @Column(unique = true)
    private String barcode;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String unit;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @Column(nullable = false)
    private Integer minStockLevel = 0;

    @Size(max = 100)
    private String category;
}