package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.CarOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarOwnerRepository extends JpaRepository<CarOwner, Long> {

    // Find by email (useful for login or unique validation)
    Optional<CarOwner> findByLicensePlate(String licensePlate);

    // Check if email already exists
    boolean existsByLicensePlate(String licensePlate);

    // Find by phone number
    Optional<CarOwner> findByAltPhone(String altPhone);

    Optional<CarOwner> findByUniqueId(Integer uniqueId);

    Optional<CarOwner> findByUserId(Long userId);
}


