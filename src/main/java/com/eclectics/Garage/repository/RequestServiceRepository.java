package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestServiceRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> getServiceByCarOwner_UniqueId(Integer carOwnerUniqueId);
    List<ServiceRequest> getServiceByGarage_GarageId(Long garageId);
}
