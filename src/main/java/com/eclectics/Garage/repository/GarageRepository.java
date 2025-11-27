package com.eclectics.Garage.repository;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GarageRepository extends JpaRepository<Garage, Long>, JpaSpecificationExecutor<Garage> {
    Optional<Garage> findByGarageId(Long garageId);
    Optional<Garage> findByBusinessName(String businessName);
    Optional<Garage> findByUserId(Long userId);
    Optional<Garage> findByUser(User user);
}
