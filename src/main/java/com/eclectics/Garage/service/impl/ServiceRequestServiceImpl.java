package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.*;
import com.eclectics.Garage.service.ServiceRequestService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@org.springframework.stereotype.Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private static final Logger logger = Logger.getLogger(ServiceRequestServiceImpl.class.getName());

    private final RequestServiceRepository requestServiceRepository;
    private final CarOwnerRepository carOwnerRepository;
    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;
    private final SeverityCategoryRepository severityCategoryRepository;

    public ServiceRequestServiceImpl(
            RequestServiceRepository requestServiceRepository,
            SeverityCategoryRepository severityCategoryRepository,
            CarOwnerRepository carOwnerRepository,
            ServiceRepository serviceRepository,
            GarageRepository garageRepository
    ) {
        this.requestServiceRepository = requestServiceRepository;
        this.carOwnerRepository = carOwnerRepository;
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.severityCategoryRepository = severityCategoryRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", allEntries = true)
    })
    public ServiceRequest createRequest(Integer carOwnerUniqueId, Long garageId, Long serviceId, Long severityId) {
        logger.info(String.format("Creating new service request for CarOwner ID: %d, Garage ID: %d, Service ID: %d, Severity ID: %d",
                carOwnerUniqueId, garageId, serviceId, severityId));

        CarOwner carOwner = carOwnerRepository.findByUniqueId(carOwnerUniqueId)
                .orElseThrow(() -> {
                    logger.warning("CarOwner with ID " + carOwnerUniqueId + " not found.");
                    return new ResourceNotFoundException("Car Owner with this id does not exist");
                });

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> {
                    logger.warning("Garage with ID " + garageId + " not found.");
                    return new ResourceNotFoundException("Garage with this id does not exist");
                });

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    logger.warning("Service with ID " + serviceId + " not found.");
                    return new ResourceNotFoundException("Service with this id does not exist");
                });

        SeverityCategories severityCategory = severityCategoryRepository.findById(severityId)
                .orElseThrow(() -> {
                    logger.warning("Severity Category with ID " + severityId + " not found.");
                    return new ResourceNotFoundException("Severity with this id does not exist");
                });

        ServiceRequest request = new ServiceRequest();
        request.setCarOwner(carOwner);
        request.setGarage(garage);
        request.setService(service);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setSeverityCategories(severityCategory);

        ServiceRequest savedRequest = requestServiceRepository.save(request);
        logger.info("Service request successfully created.");
        return savedRequest;
    }

    @Override
    @Cacheable(value = "allServiceRequests")
    public List<ServiceRequest> getAllRequests() {
        logger.info("Fetching all service requests...");
        List<ServiceRequest> requests = requestServiceRepository.findAll();
        logger.info("Total service requests fetched: " + requests.size());
        return requests;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", key = "#requestId")
    })
    public ServiceRequest updateStatus(Long requestId, RequestStatus status, Long severityId) {
        logger.info(String.format("Updating status for request ID: %d to %s", requestId, status));

        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.warning("Service request with ID " + requestId + " not found.");
                    return new ResourceNotFoundException("Service with this id " + requestId + " not found");
                });

        if (status != null) serviceRequest.setStatus(status);
        serviceRequest.setUpdatedAt(LocalDateTime.now());

        if (severityId != null) {
            severityCategoryRepository.findById(severityId).ifPresentOrElse(
                    serviceRequest::setSeverityCategories,
                    () -> logger.warning("Severity category with ID " + severityId + " not found. Keeping previous severity.")
            );
        }

        ServiceRequest updatedRequest = requestServiceRepository.save(serviceRequest);
        logger.info("Service request updated successfully with ID: " + requestId);
        return updatedRequest;
    }

    @Override
    @Cacheable(value = "requestsByCarOwner", key = "#carOwnerUniqueId")
    public List<ServiceRequest> getRequestsByCarOwner(Integer carOwnerUniqueId) {
        logger.info("Fetching service requests for CarOwner with ID: " + carOwnerUniqueId);
        List<ServiceRequest> requests = requestServiceRepository.getServiceByCarOwner_UniqueId(carOwnerUniqueId);
        logger.info("Total requests found for CarOwner " + carOwnerUniqueId + ": " + requests.size());
        return requests;
    }

    @Override
    @Cacheable(value = "requestsByGarage", key = "#garageId")
    public List<ServiceRequest> getRequestsByGarage(Long garageId) {
        logger.info("Fetching service requests for Garage with ID: " + garageId);
        List<ServiceRequest> requests = requestServiceRepository.getServiceByGarage_GarageId(garageId);
        logger.info("Total requests found for Garage " + garageId + ": " + requests.size());
        return requests;
    }

    @Override
    @Cacheable(value = "requestById", key = "#requestId")
    public Optional<ServiceRequest> getRequestById(Long requestId) {
        logger.info("Fetching service request by ID: " + requestId);
        Optional<ServiceRequest> request = requestServiceRepository.findById(requestId);
        if (request.isPresent()) {
            logger.info("Service request found with ID: " + requestId);
        } else {
            logger.warning("No service request found with ID: " + requestId);
        }
        return request;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", key = "#id")
    })
    public void deleteServiceRequest(Long id) {
        logger.info("Attempting to delete service request with ID: " + id);
        try {
            requestServiceRepository.deleteById(id);
            logger.info("Service request deleted successfully with ID: " + id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting service request with ID: " + id, e);
            throw e;
        }
    }
}
