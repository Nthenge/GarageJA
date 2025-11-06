package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.ServiceMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceCategoryRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServicesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServicesService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceMapper mapper;

    private final Map<Long, List<ServiceResponseDTO>> garageServiceCache = new ConcurrentHashMap<>();
    private final Set<Long> validCategoryIds = ConcurrentHashMap.newKeySet();
    private final Set<String> existingServiceNames = ConcurrentHashMap.newKeySet();

    public ServiceServiceImpl(ServiceRepository serviceRepository,
                              GarageRepository garageRepository,
                              ServiceCategoryRepository serviceCategoryRepository,
                              ServiceMapper mapper) {
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.mapper = mapper;

        serviceCategoryRepository.findAll().forEach(cat -> validCategoryIds.add(cat.getId()));

        serviceRepository.findAll().forEach(svc -> existingServiceNames.add(svc.getServiceName().toLowerCase()));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", allEntries = true)
    })
    public void createService(ServiceRequestDTO serviceRequestDTO) {
        logger.info("Creating new service: {}", serviceRequestDTO.getServiceName());

        if (existingServiceNames.contains(serviceRequestDTO.getServiceName().toLowerCase())) {
            throw new IllegalArgumentException("Service name already exists");
        }

        if (!validCategoryIds.contains(serviceRequestDTO.getCategoryId())) {
            throw new ResourceNotFoundException("Invalid category ID");
        }

        Service service = mapper.toEntity(serviceRequestDTO);
        Garage garage = garageRepository.findByGarageId(serviceRequestDTO.getGarageId())
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        ServiceCategories category = serviceCategoryRepository.findById(serviceRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        service.setGarage(garage);
        service.setServiceCategories(category);

        Service savedService = serviceRepository.save(service);
        existingServiceNames.add(service.getServiceName().toLowerCase());

        garageServiceCache.computeIfAbsent(garage.getGarageId(), k -> new ArrayList<>())
                .add(mapper.toResponseDTO(savedService));

        logger.info("Service created successfully with ID: {}", savedService.getId());
    }

    @Override
    @Cacheable(value = "serviceById", key = "#id")
    public Optional<ServiceResponseDTO> getServiceById(Long id) {
        logger.info("Fetching service by ID: {}", id);
        return serviceRepository.findById(id).map(mapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "allServices")
    public List<ServiceResponseDTO> getAllServices() {
        logger.info("Fetching all services");
        List<Service> services = serviceRepository.findAll();
        return mapper.toResponseDTOList(services);
    }

    @Override
    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<ServiceResponseDTO> getServicesByGarageId(Long garageId) {
        logger.info("Fetching services for garage ID: {}", garageId);

        if (garageServiceCache.containsKey(garageId)) {
            logger.debug("Cache hit for garage ID: {}", garageId);
            return garageServiceCache.get(garageId);
        }

        List<Service> services = serviceRepository.findByGarage_GarageId(garageId);
        List<ServiceResponseDTO> dtos = mapper.toResponseDTOList(services);

        garageServiceCache.put(garageId, dtos);
        return dtos;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public void updateService(Long id, ServiceRequestDTO serviceRequestDTO) {
        logger.info("Updating service with ID: {}", id);

        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        mapper.updateFromDTO(serviceRequestDTO, existingService);
        Service updated = serviceRepository.save(existingService);

        existingServiceNames.add(updated.getServiceName().toLowerCase());
        garageServiceCache.computeIfPresent(updated.getGarage().getGarageId(), (key, oldList) -> {
            oldList.removeIf(svc -> Objects.equals(svc.getId(), id));
            oldList.add(mapper.toResponseDTO(updated));
            return oldList;
        });

        logger.info("Service with ID {} updated successfully", id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", key = "#id")
    })
    public void deleteService(Long id) {
        logger.warn("Deleting service with ID: {}", id);

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        serviceRepository.deleteById(id);
        existingServiceNames.remove(service.getServiceName().toLowerCase());
        garageServiceCache.remove(service.getGarage().getGarageId());

        logger.info("Service with ID {} deleted successfully", id);
    }

    @Override
    public long countGaragesByServiceName(String serviceName) {
        logger.info("Counting garages with service name: {}", serviceName);
        return serviceRepository.countByServiceName(serviceName);
    }
}
