package com.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "location_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}