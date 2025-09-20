package com.quickcommerce.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import com.quickcommerce.shared.entity.BaseEntity;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "auth")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions", schema = "auth",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

}
