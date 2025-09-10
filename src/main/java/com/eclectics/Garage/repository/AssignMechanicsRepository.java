package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.AssignMechanics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignMechanicsRepository extends JpaRepository<AssignMechanics, Long> {
    List<AssignMechanics> findByService_Id(Long id);
    List<AssignMechanics> findByMechanicId(Long id);
}
