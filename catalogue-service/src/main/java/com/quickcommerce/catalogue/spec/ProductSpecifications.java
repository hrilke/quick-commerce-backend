package com.quickcommerce.catalogue.spec;

import com.quickcommerce.catalogue.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.Set;

public class ProductSpecifications {

    public static Specification<Product> titleOrDescriptionContains(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<Product> inCategories(Set<String> categorySlugs) {
        if (categorySlugs == null || categorySlugs.isEmpty()) return null;
        return (root, cq, cb) -> {
            var join = root.join("categories");
            cq.distinct(true);
            return join.get("slug").in(categorySlugs);
        };
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) {
            return (root, cq, cb) -> cb.between(root.get("mrp"), min, max);
        }
        if (min != null) return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("mrp"), min);
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("mrp"), max);
    }
}
