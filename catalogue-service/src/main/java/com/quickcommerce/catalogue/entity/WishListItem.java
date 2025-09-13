package com.quickcommerce.catalogue.entity;

import com.quickcommerce.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(
        name = "wish_list_items",
        schema = "catalogue",
        indexes = {
                @Index(name = "idx_wishlistitem_userid", columnList = "user_id")
        }
)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WishListItem extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}


