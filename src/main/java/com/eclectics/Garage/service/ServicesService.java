package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServicesService {
    Service createService(Service service);
    Optional<Service> getServiceById(Long id);
    List<Service> getAllServices();
    List<Service> getServicesByGarageId(Long garageId);
    Service updateService(Long id, Service service);
    String deleteService(Long id);
}

