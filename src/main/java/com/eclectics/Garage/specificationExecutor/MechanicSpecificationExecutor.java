package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.Mechanic;
import org.springframework.data.jpa.domain.Specification;

public class MechanicSpecificationExecutor {
    public static Specification<Mechanic> vehicleBrandsContains(String vehicleBrands) {
        return (root, query, cb) ->
                vehicleBrands == null || vehicleBrands.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("vehicleBrands")),
                        "%" + vehicleBrands.toLowerCase() + "%"
                );
    }

    public static Specification<Mechanic> nationalIdEquals(Integer nationalIdNumber) {
        return (root, query, cb) ->
                nationalIdNumber == null
                        ? null
                        : cb.equal(root.get("nationalIdNumber"), nationalIdNumber);
    }

    public static Specification<Mechanic> garageIdEquals(Long garageId) {
        return (root, query, cb) ->
                garageId == null
                        ? null
                        : cb.equal(root.join("garage").get("garageId"), garageId);
    }
}
