package com.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @Size(max = 500)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String country;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Location> locations = new ArrayList<>();
}