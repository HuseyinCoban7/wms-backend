package com.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"warehouse_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String code;

    @Size(max = 200)
    private String description;

    @Size(max = 50)
    private String aisle;

    @Size(max = 50)
    private String rack;

    @Size(max = 50)
    private String bin;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
}