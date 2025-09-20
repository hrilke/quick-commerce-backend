package com.quickcommerce.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import com.quickcommerce.shared.entity.BaseEntity;

@Entity
@Table(name = "permissions", schema = "auth")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 128)
    private String name;

}
