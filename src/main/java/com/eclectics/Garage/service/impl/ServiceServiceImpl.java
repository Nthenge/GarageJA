package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServicesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;


import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServicesService {

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
        if (service.getGarage() != null && service.getGarage().getGarageId() != null) {
            Long garageAdminId = service.getGarage().getGarageId();

            Garage garage = garageRepository.findByGarageId(garageAdminId)
                    .orElseThrow(() -> new RuntimeException("Garage with this id " + garageAdminId + " not found"));

            service.setGarage(garage);
        }
        return serviceRepository.save(service);
    }

    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<Service> getAllServicesByGarageId(Long garageId) {
        return serviceRepository.findByGarage_GarageId(garageId);
    }

    @Override
    @Cacheable(value = "serviceById", key = "#id")
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    @Cacheable(value = "allServices")
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<Service> getServicesByGarageId(Long garageId) {
        return serviceRepository.findByGarage_GarageId(garageId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public Service updateService(Long id, Service service) {
        return serviceRepository.findById(id).map(existingService -> {
            if (service.getServiceName() != null) existingService.setServiceName(service.getServiceName());
            if (service.getDescription() != null) existingService.setDescription(service.getDescription());
            if (service.getPrice() != null) existingService.setPrice(service.getPrice());
            if (service.getGarage() != null) existingService.setGarage(service.getGarage());
            if (service.getServiceCategories() != null) existingService.setServiceCategories(service.getServiceCategories());
            return serviceRepository.save(existingService);
        }).orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public String deleteService(Long id) {
        serviceRepository.deleteById(id);
        return "Service Deleted";
    }
}
