package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.CarOwner;
import org.springframework.data.jpa.domain.Specification;

public class CarOwnerSpecificationExecutor {
    public static Specification<CarOwner> carOwnerUrlContains(Integer uniqueId){
        return (root, query, cb) -> {
            if (uniqueId == null ){
                return cb.conjunction();
            }
            return cb.equal(root.get("uniqueId"), uniqueId);
        };
    }

    public static Specification<CarOwner> CarOwnerPlateContains(String licensePlate){
        return (root, query, cb) -> {
            if (licensePlate == null || licensePlate.isBlank()){
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("licensePlate")),
                    "%" + licensePlate.toLowerCase() + "%");
        };
    }
}
