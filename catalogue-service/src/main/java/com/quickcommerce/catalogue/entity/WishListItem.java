package com.quickcommerce.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class WishListItem extends CartItem {
}


