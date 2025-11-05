package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.mapper.ServiceMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServicesService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServicesService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceMapper mapper;

    public ServiceServiceImpl(ServiceRepository serviceRepository, GarageRepository garageRepository, ServiceCategoryRepository serviceCategoryRepository, ServiceMapper mapper) {
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.mapper = mapper;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", allEntries = true)
    })
    public void createService(ServiceRequestDTO serviceRequestDTO) {
        logger.info("Creating new service: {}", serviceRequestDTO.getServiceName());

        Service service = mapper.toEntity(serviceRequestDTO);
        if (service.getGarage() != null && service.getGarage().getGarageId() != null) {
            Long garageAdminId = service.getGarage().getGarageId();
            logger.debug("Fetching garage with ID: {}", garageAdminId);

            Garage garage = garageRepository.findByGarageId(serviceRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
            ServiceCategories category = serviceCategoryRepository.findById(serviceRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            service.setGarage(garage);
            service.setServiceCategories(category);

            service.setGarage(garage);
        }

        Service savedService = serviceRepository.save(service);
        logger.info("Service created successfully with ID: {}", savedService.getId());
    }

    @Override
    @Cacheable(value = "serviceById", key = "#id")
    public Optional<ServiceResponseDTO> getServiceById(Long id) {
        logger.info("Fetching service by ID: {}", id);
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            logger.debug("Service found: {}", service.get().getServiceName());
        } else {
            logger.warn("No service found with ID: {}", id);
        }
        return mapper.toOptionalResponseDTO(service);
    }

    @Override
    @Cacheable(value = "allServices")
    public List<ServiceResponseDTO> getAllServices() {
        logger.info("Fetching all services");
        List<Service> services = serviceRepository.findAll();
        logger.debug("Total services found: {}", services.size());
        return mapper.toResponseDTOList(services);
    }

    @Override
    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<ServiceResponseDTO> getServicesByGarageId(Long garageId) {
        logger.info("Fetching services for garage ID: {}", garageId);
        List<Service> services = serviceRepository.findByGarage_GarageId(garageId);
        logger.debug("Found {} services for garage ID {}", services.size(), garageId);
        return mapper.toResponseDTOList(services);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public void updateService(Long id, ServiceRequestDTO serviceRequestDTO) {
        logger.info("Updating service with ID: {}", id);
        serviceRepository.findById(id).map(existingService -> {
            mapper.updateFromDTO(serviceRequestDTO, existingService);

            Service updated = serviceRepository.save(existingService);
            logger.info("Service with ID {} updated successfully", id);
            return mapper.toResponseDTO(updated);
        }).orElseThrow(() -> {
            logger.error("Service with ID {} not found for update", id);
            return new ResourceNotFoundException("Service not found");
        });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public void deleteService(Long id) {
        logger.warn("Deleting service with ID: {}", id);
        serviceRepository.deleteById(id);
        logger.info("Service with ID {} deleted successfully", id);
    }

    @Override
    public long countGaragesByServiceName(String serviceName) {
        logger.info("Counting services (representing garages) with name: {}", serviceName);

        long count = serviceRepository.countByServiceName(serviceName);

        logger.debug("Found {} services matching name: {}", count, serviceName);
        return count;
    }
}
