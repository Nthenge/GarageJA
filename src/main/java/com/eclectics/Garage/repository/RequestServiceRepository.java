package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestServiceRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> getServiceByCarOwnerId(Long carOwnerUniqueId);
    List<ServiceRequest> getServiceByGarageId(Long id);
}
