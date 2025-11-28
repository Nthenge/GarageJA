package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecificationExecutor {

    public static Specification<Service> serviceNameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("serviceName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Service> garageNameContains(String garageName) {
        return (root, query, cb) -> {
            if (garageName == null || garageName.trim().isEmpty()) {
                return cb.conjunction();  // <- Skip this filter
            }
            return cb.like(
                    cb.lower(root.join("garages").get("businessName")),
                    "%" + garageName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Service> priceEquals(Double price) {
        return (root, query, cb) -> {
            if (price == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("price"), price);
        };
    }

    public static Specification<Service> keywordSearch(String keyword) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction(); // return all results
            }

            String kw = "%" + keyword.toLowerCase() + "%";

            Join<Service, Garage> garageJoin = root.joinSet("garages", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("serviceName")), kw),
                    cb.like(cb.lower(root.get("description")), kw),
                    cb.like(cb.lower(garageJoin.get("businessName")), kw)
            );
        };
    }


}
