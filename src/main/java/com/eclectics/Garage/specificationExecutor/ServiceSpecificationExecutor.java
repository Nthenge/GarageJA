package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.Service;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecificationExecutor {
    public static Specification<Service> serviceNameContains(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("serviceName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Service> garageNameContains(String garageName) {
        return (root, query, cb) -> {
            if (garageName == null) return null;

            return cb.like(
                    cb.lower(root.join("garages").get("businessName")),
                    "%" + garageName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Service> priceEquals(Double price) {
        return (root, query, cb) ->
                price == null ? null : cb.equal(root.get("price"), price);
    }
}
