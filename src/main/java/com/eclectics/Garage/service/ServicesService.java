package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ServicesService {
    void createService(ServiceRequestDTO serviceRequestDTO);
    Optional<ServiceResponseDTO> getServiceById(Long id);
    List<ServiceResponseDTO> getAllServices();
    List<ServiceResponseDTO> getServicesByGarageId(Long garageId);
    void updateService(Long id, ServiceRequestDTO serviceRequestDTO);
    void deleteService(Long id);
    long countGaragesByServiceName(String serviceName);
}

