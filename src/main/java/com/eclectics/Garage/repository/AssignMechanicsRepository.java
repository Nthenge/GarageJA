package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.AssignMechanics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AssignMechanicsRepository extends JpaRepository<AssignMechanics, Long>, JpaSpecificationExecutor<AssignMechanics> {
    List<AssignMechanics> findByService_Id(Long id);
    List<AssignMechanics> findByMechanicId(Long id);
}
