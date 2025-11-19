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
import java.util.stream.Collectors;

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
    private final Map<String, List<ServiceResponseDTO>> searchServiceCache = new ConcurrentHashMap<>();


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
            @CacheEvict(value = "servicesByGarage", allEntries = true)
    })
    public ServiceResponseDTO createService(Long categoryId, ServiceRequestDTO dto) {
        logger.info("Creating new service: {}", dto.getServiceName());

        if (existingServiceNames.contains(dto.getServiceName().toLowerCase())) {
            throw new IllegalArgumentException("Service name already exists");
        }
        if (!validCategoryIds.contains(categoryId)) {
            throw new ResourceNotFoundException("Invalid category ID");
        }
//        Garage garage = garageRepository.findByGarageId(dto.getGarageId())
//                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
        ServiceCategories category = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Service service = mapper.toEntity(dto);
        service.setServiceCategories(category);
//        service.getGarages().add(garage);
//        garage.getOfferedServices().add(service);
        serviceRepository.save(service);
//        garageRepository.save(garage);
        existingServiceNames.add(service.getServiceName().toLowerCase());

        logger.info("Service created successfully with ID: {}", service.getId());
        return mapper.toResponseDTO(service);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "servicesByGarage", allEntries = true)
    })
    public ServiceResponseDTO assignServiceToGarage(Long serviceId, Long garageId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        // add service to garage
        garage.getOfferedServices().add(service);
        service.getGarages().add(garage);

        // persist relationship
        garageRepository.save(garage);

        // clear cache for this garage
        garageServiceCache.remove(garageId);

        logger.info("Service '{}' assigned to Garage '{}'", service.getServiceName(), garage.getBusinessName());
        return mapper.toResponseDTO(service);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "servicesByGarage", key = "#garageId", allEntries = false),
            @CacheEvict(value = "searchServices", allEntries = true)
    })
    public void assignMultipleServicesToGarage(Long garageId, List<Long> serviceIds) {
        logger.info("Assigning services {} to garage ID: {}", serviceIds, garageId);

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        List<Service> services = serviceRepository.findAllById(serviceIds);

        // 1. CRITICAL: Check if all requested services were found
        if (services.size() != serviceIds.size()) {
            Set<Long> foundIds = services.stream().map(Service::getId).collect(Collectors.toSet());
            List<Long> notFoundIds = serviceIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            if (!notFoundIds.isEmpty()) {
                logger.warn("Service IDs not found: {}", notFoundIds);
                throw new ResourceNotFoundException("The following service IDs were not found: " + notFoundIds);
            }
        }

        int servicesAdded = 0;
        // 2. Establish the M-M relationship on both sides for bi-directional consistency
        for (Service service : services) {
            // .add returns true if the element was added (i.e., not a duplicate)
            if (garage.getOfferedServices().add(service)) {
                service.getGarages().add(garage);
                servicesAdded++;
            }
        }

        // 3. Persist the change
        if (servicesAdded > 0) {
            garageRepository.save(garage);
            logger.info("{} new services assigned to Garage '{}'", servicesAdded, garage.getBusinessName());
        } else {
            logger.info("No new services assigned; all services were already offered by Garage '{}'", garage.getBusinessName());
        }

        // 4. Clear the garage-specific cache manually as well, just in case
        garageServiceCache.remove(garageId);
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
        return mapper.toResponseDTOList(serviceRepository.findAll());
    }

    @Override
    @Cacheable(value = "servicesByGarage", key = "#garageId")
    public List<ServiceResponseDTO> getServicesByGarageId(Long garageId) {
        logger.info("Fetching services for garage ID: {}", garageId);

        if (garageServiceCache.containsKey(garageId)) {
            logger.debug("Cache hit for garage ID: {}", garageId);
            return garageServiceCache.get(garageId);
        }

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        List<ServiceResponseDTO> dtos = garage.getOfferedServices()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());

        garageServiceCache.put(garageId, dtos);
        return dtos;
    }

    @Override
    @Cacheable(
            value = "searchServices",
            key = "T(java.util.Objects).toString(#serviceName) + '-' + " +
                    "T(java.util.Objects).toString(#garageName) + '-' + " +
                    "T(java.util.Objects).toString(#price)"
    )
    public List<ServiceResponseDTO> searchServices(String serviceName, Double price, String garageName){
        String cacheKey = (serviceName == null ? "null" : serviceName.toLowerCase()) + "-" +
                (garageName == null ? "null" : garageName.toLowerCase()) + "-" +
                (price == null ? "null" : price);

        if (searchServiceCache.containsKey(cacheKey)) {
            logger.debug("Cache hit for search key: {}", cacheKey);
            return searchServiceCache.get(cacheKey);
        }

        logger.info("Cache miss for search key: {}, querying database...", cacheKey);

        List<Service> services;

        if (serviceName != null && garageName != null && price != null) {
            services = serviceRepository.findByServiceNameContainingIgnoreCaseAndGarages_BusinessNameContainingIgnoreCaseAndPrice(
                    serviceName, garageName, price);
        } else if (serviceName != null && garageName != null) {
            services = serviceRepository.findByServiceNameContainingIgnoreCaseAndGarages_BusinessNameContainingIgnoreCase(
                    serviceName, garageName);
        } else if (garageName != null && price != null) {
            services = serviceRepository.findByGarages_BusinessNameContainingIgnoreCaseAndPrice(garageName, price);
        } else if (serviceName != null && price != null) {
            services = serviceRepository.findByServiceNameContainingIgnoreCaseAndPrice(serviceName, price);
        } else if (serviceName != null) {
            services = serviceRepository.findByServiceNameContainingIgnoreCase(serviceName);
        } else if (garageName != null) {
            services = serviceRepository.findByGarages_BusinessNameContainingIgnoreCase(garageName);
        } else if (price != null) {
            services = serviceRepository.findByPrice(price);
        } else {
            services = serviceRepository.findAll();
        }

        if (services.isEmpty()) {
            logger.warn("No services found for search key: {}", cacheKey);
            throw new ResourceNotFoundException("No services found matching your search criteria.");
        }

        List<ServiceResponseDTO> result = mapper.toResponseDTOList(services);
        searchServiceCache.put(cacheKey, result);

        return result;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true),
            @CacheEvict(value = "serviceById", allEntries = true),
            @CacheEvict(value = "searchServices", allEntries = true)
    })
    public ServiceRequestDTO updateService(Long id, ServiceRequestDTO serviceRequestDTO) {
        logger.info("Updating service with ID: {}", id);

        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        mapper.updateFromDTO(serviceRequestDTO, existingService);

        Service updated = serviceRepository.save(existingService);

        existingServiceNames.add(updated.getServiceName().toLowerCase());

        for (Garage g : updated.getGarages()) {
            garageServiceCache.computeIfPresent(g.getGarageId(), (key, oldList) -> {
                oldList.removeIf(svc -> Objects.equals(svc.getId(), id));
                oldList.add(mapper.toResponseDTO(updated));
                return oldList;
            });
        }

        logger.info("Service with ID {} updated successfully", id);
        return serviceRequestDTO;
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServices", allEntries = true),
            @CacheEvict(value = "servicesByGarage", allEntries = true)
    })
    public ServiceResponseDTO deleteService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        ServiceResponseDTO responseDTO = mapper.toResponseDTO(service);

        for (Garage g : service.getGarages()) {
            g.getOfferedServices().remove(service);
            garageRepository.save(g);
        }
        serviceRepository.delete(service);

        existingServiceNames.remove(service.getServiceName().toLowerCase());

        logger.info("Service with ID {} deleted successfully", serviceId);

        return responseDTO;
    }


    @Override
    public long countGaragesByServiceName(String serviceName) {
        return serviceRepository.countByServiceName(serviceName);
    }
}
