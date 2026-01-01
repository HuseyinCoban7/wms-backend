package com.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String address;

    @Column(nullable = false)
    private Boolean active = true;
}