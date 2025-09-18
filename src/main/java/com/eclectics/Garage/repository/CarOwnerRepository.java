package com.eclectics.Garage.repository;

import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.model.CarOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarOwnerRepository extends JpaRepository<CarOwner, Long> {
    Optional<CarOwnerResponseDTO> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
    Optional<CarOwner> findByUniqueId(Integer uniqueId);
    Optional<CarOwner> findByUserId(Long userId);
}


