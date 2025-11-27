package com.eclectics.Garage.repository;

import com.eclectics.Garage.dto.CarOwnerResponseDTO;
import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarOwnerRepository extends JpaRepository<CarOwner, Long>, JpaSpecificationExecutor<CarOwner> {
    Optional<CarOwnerResponseDTO> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
    Optional<CarOwner> findByUniqueId(Integer uniqueId);
    Optional<CarOwner> findByUserId(Long userId);
    Optional<CarOwner> findByUser(User user);
}


