package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByGarage_GarageId(Long garageId);

    //below to be used to count number of garages offering a service using service name, because a garage can only offer one instance of the same service, so the number of serviceNames will be equal to the tally of garages
    @Query("SELECT COUNT(DISTINCT s.garage.garageId) FROM Service s WHERE s.serviceName = :serviceName")
    long countByServiceName(String serviceName);

    Optional<Service> findById(Long id);
}

