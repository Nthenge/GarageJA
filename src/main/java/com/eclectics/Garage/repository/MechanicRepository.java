package com.eclectics.Garage.repository;

import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {
    List<Mechanic> findByGarageId(Long garageId);
    Optional<Mechanic> findMechanicByNationalIdNumber(Integer nationalIdNumber);
    Optional<Mechanic> findByUserId(Long id);
    Optional<Mechanic> findByUser(User user);
}

