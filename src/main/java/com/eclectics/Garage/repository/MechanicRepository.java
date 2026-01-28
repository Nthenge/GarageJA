package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface MechanicRepository extends JpaRepository<Mechanic, Long>, JpaSpecificationExecutor<Mechanic> {
    Optional<Mechanic> findMechanicByNationalIdNumber(Integer nationalIdNumber);
    List<Mechanic> findByGarage_GarageId(Long garageId);
    Optional<Mechanic> findByUserId(Long id);
    Optional<Mechanic> findByUser(User user);
}

