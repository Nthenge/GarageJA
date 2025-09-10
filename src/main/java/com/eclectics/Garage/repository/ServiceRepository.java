package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByGarage_GarageId(Long garageId);
}

