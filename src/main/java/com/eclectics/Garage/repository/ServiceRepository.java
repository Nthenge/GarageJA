package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    // Find services by a single garage ID
    @Query("SELECT s FROM Service s JOIN s.garages g WHERE g.garageId = :garageId")
    List<Service> findByGarageId(Long garageId);

    // Count the number of distinct garages offering a service by name
    @Query("SELECT COUNT(DISTINCT g.garageId) FROM Service s JOIN s.garages g WHERE s.serviceName = :serviceName")
    long countByServiceName(String serviceName);

    Optional<Service> findById(Long id);
    List<Service> findByPrice(Double price);

    List<Service> findByServiceNameContainingIgnoreCaseAndPrice(String serviceName, Double price);
    List<Service> findByServiceNameContainingIgnoreCase(String serviceName);

    List<Service> findByGarages_BusinessNameContainingIgnoreCase(String garageName);
    List<Service> findByGarages_BusinessNameContainingIgnoreCaseAndPrice(String garageName, Double price);
    List<Service> findByServiceNameContainingIgnoreCaseAndGarages_BusinessNameContainingIgnoreCase(
            String serviceName, String garageName
    );
    List<Service> findByServiceNameContainingIgnoreCaseAndGarages_BusinessNameContainingIgnoreCaseAndPrice(
            String serviceName, String garageName, Double price
    );
}
