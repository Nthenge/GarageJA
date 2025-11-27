package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.Garage;
import org.springframework.data.jpa.domain.Specification;

public class GarageSpecificationExecutor {
    public static Specification<Garage> businessNameContains(String businessName) {
        return (root, query, cb) -> {
            if (businessName == null || businessName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("businessName")),
                    "%" + businessName.toLowerCase() + "%");
        };
    }

    public static Specification<Garage> physicalAddressContains(String physicalBusinessAddress) {
        return (root, query, cb) -> {
            if (physicalBusinessAddress == null || physicalBusinessAddress.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("physicalBusinessAddress")),
                    "%" + physicalBusinessAddress.toLowerCase() + "%");
        };
    }

}
