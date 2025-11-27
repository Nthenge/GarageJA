package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    // Count the number of distinct garages offering a service by name
    @Query("SELECT COUNT(DISTINCT g.garageId) FROM Service s JOIN s.garages g WHERE s.serviceName = :serviceName")
    long countByServiceName(String serviceName);

    Optional<Service> findById(Long id);
}
