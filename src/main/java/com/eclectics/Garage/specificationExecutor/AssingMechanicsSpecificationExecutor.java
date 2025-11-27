package com.eclectics.Garage.specificationExecutor;

import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.model.AssignMechanics;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AssingMechanicsSpecificationExecutor {
    public static Specification<AssignMechanics> statusEquals(AssignMechanicStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<AssignMechanics> assignedOnDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return null;

            var startOfDay = date.atStartOfDay();
            var endOfDay = date.plusDays(1).atStartOfDay();

            return cb.between(root.get("assignedAt"), startOfDay, endOfDay);
        };
    }

    public static Specification<AssignMechanics> requestIdEquals(Long requestId) {
        return (root, query, cb) ->
                requestId == null ? null : cb.equal(root.get("service").get("id"), requestId);
    }

    public static Specification<AssignMechanics> mechanicIdEquals(Long mechanicId) {
        return (root, query, cb) ->
                mechanicId == null ? null : cb.equal(root.get("mechanic").get("nationalIdNumber"), mechanicId);
    }

}
