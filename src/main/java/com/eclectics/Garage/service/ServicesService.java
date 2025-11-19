package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Optional;

public interface ServicesService {
    ServiceResponseDTO createService(Long categoryId, ServiceRequestDTO serviceRequestDTO);

    @Caching(evict = {
            @CacheEvict(value = "servicesByGarage", allEntries = true)
    })
    ServiceResponseDTO assignServiceToGarage(Long serviceId, Long garageId);
    Optional<ServiceResponseDTO> getServiceById(Long id);
    List<ServiceResponseDTO> getAllServices();
    List<ServiceResponseDTO> getServicesByGarageId(Long garageId);
    List<ServiceResponseDTO> searchServices(String serviceName, Double price,String garageName);
    ServiceRequestDTO updateService(Long id, ServiceRequestDTO serviceRequestDTO);
    ServiceResponseDTO deleteService(Long id);
    long countGaragesByServiceName(String serviceName);
    void assignMultipleServicesToGarage(Long garageId, List<Long> serviceIds);
}

