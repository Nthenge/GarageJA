package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServicesService;
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

    public ServiceServiceImpl(ServiceRepository serviceRepository, GarageRepository garageRepository) {
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", allEntries = true)
    })
    public Service createService(Service service) {
        logger.info("Creating new service: {}", service.getServiceName());

        if (service.getGarage() != null && service.getGarage().getGarageId() != null) {
            Long garageAdminId = service.getGarage().getGarageId();
            logger.debug("Fetching garage with ID: {}", garageAdminId);

            Garage garage = garageRepository.findByGarageId(garageAdminId)
                    .orElseThrow(() -> {
                        logger.error("Garage with ID {} not found", garageAdminId);
                        return new RuntimeException("Garage with this id " + garageAdminId + " not found");
                    });

            service.setGarage(garage);
        }

        Service savedService = serviceRepository.save(service);
        logger.info("Service created successfully with ID: {}", savedService.getId());
        return savedService;
    }

    @Override
    @Cacheable(value = "serviceById", key = "#id")
    public Optional<Service> getServiceById(Long id) {
        logger.info("Fetching service by ID: {}", id);
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            logger.debug("Service found: {}", service.get().getServiceName());
        } else {
            logger.warn("No service found with ID: {}", id);
        }
        return service;
    }

    @Override
    @Cacheable(value = "allServices")
    public List<Service> getAllServices() {
        logger.info("Fetching all services");
        List<Service> services = serviceRepository.findAll();
        logger.debug("Total services found: {}", services.size());
        return services;
    }

    @Override
    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<Service> getServicesByGarageId(Long garageId) {
        logger.info("Fetching services for garage ID: {}", garageId);
        List<Service> services = serviceRepository.findByGarage_GarageId(garageId);
        logger.debug("Found {} services for garage ID {}", services.size(), garageId);
        return services;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public Service updateService(Long id, Service service) {
        logger.info("Updating service with ID: {}", id);
        return serviceRepository.findById(id).map(existingService -> {
            if (service.getServiceName() != null) existingService.setServiceName(service.getServiceName());
            if (service.getDescription() != null) existingService.setDescription(service.getDescription());
            if (service.getPrice() != null) existingService.setPrice(service.getPrice());
            if (service.getGarage() != null) existingService.setGarage(service.getGarage());
            if (service.getServiceCategories() != null) existingService.setServiceCategories(service.getServiceCategories());

            Service updated = serviceRepository.save(existingService);
            logger.info("Service with ID {} updated successfully", id);
            return updated;
        }).orElseThrow(() -> {
            logger.error("Service with ID {} not found for update", id);
            return new RuntimeException("Service not found");
        });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public String deleteService(Long id) {
        logger.warn("Deleting service with ID: {}", id);
        serviceRepository.deleteById(id);
        logger.info("Service with ID {} deleted successfully", id);
        return "Service Deleted";
    }
}
