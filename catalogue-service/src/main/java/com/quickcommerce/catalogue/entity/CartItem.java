package com.quickcommerce.catalogue.entity;

import com.quickcommerce.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Entity
@Table(
        name = "cart_items",
        schema = "catalogue",
        indexes = {
                @Index(name = "idx_cartitem_userid", columnList = "user_id")
        }
)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartItem extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}


