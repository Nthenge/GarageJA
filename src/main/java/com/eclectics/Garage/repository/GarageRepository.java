package com.eclectics.Garage.repository;
import com.eclectics.Garage.model.Garage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarageRepository extends JpaRepository<Garage, Long> {
    Optional<Garage> findByGarageId(Long garageId);
    Optional<Garage> findByBusinessName(String businessName);
    Optional<Garage> findByUserId(Long userId);
}
