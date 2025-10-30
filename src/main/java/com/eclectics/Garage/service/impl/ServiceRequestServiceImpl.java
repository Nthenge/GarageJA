package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.mapper.ServiceRequestsMapper;
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
    private final ServiceRequestsMapper mapper;

    public ServiceRequestServiceImpl(
            RequestServiceRepository requestServiceRepository,
            SeverityCategoryRepository severityCategoryRepository,
            CarOwnerRepository carOwnerRepository,
            ServiceRepository serviceRepository,
            GarageRepository garageRepository,
            ServiceRequestsMapper mapper
    ) {
        this.requestServiceRepository = requestServiceRepository;
        this.carOwnerRepository = carOwnerRepository;
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.severityCategoryRepository = severityCategoryRepository;
        this.mapper = mapper;
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
                .orElseThrow(() -> new ResourceNotFoundException("Car Owner with this id does not exist"));

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage with this id does not exist"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with this id does not exist"));

        SeverityCategories severityCategory = severityCategoryRepository.findById(severityId)
                .orElseThrow(() -> new ResourceNotFoundException("Severity with this id does not exist"));

        ServiceRequest request = new ServiceRequest();
        request.setCarOwner(carOwner);
        request.setGarage(garage);
        request.setService(service);
        request.setSeverityCategories(severityCategory);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        ServiceRequest savedRequest = requestServiceRepository.save(request);
        logger.info("Service request successfully created.");
        return savedRequest;
    }


    @Override
    @Cacheable(value = "allServiceRequests")
    public List<ServiceRequestsResponseDTO> getAllRequests() {
        logger.info("Fetching all service requests...");
        List<ServiceRequest> requests = requestServiceRepository.findAll();
        logger.info("Total service requests fetched: " + requests.size());
        return mapper.toResponseList(requests);
    }

    //Look into how, data structures are implemented in java spring boot and implement them
    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", key = "#requestId")
    })
    public ServiceRequestsResponseDTO updateStatus(Long requestId, RequestStatus status, Long severityId, ServiceRequestsRequestDTO serviceRequestsRequestDTO) {

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
        return mapper.toResponse(updatedRequest);
    }


    @Override
    @Cacheable(value = "requestsByCarOwner", key = "#carOwnerUniqueId")
    public List<ServiceRequestsResponseDTO> getRequestsByCarOwner(Integer carOwnerUniqueId) {
        logger.info("Fetching service requests for CarOwner with ID: " + carOwnerUniqueId);
        List<ServiceRequest> requests = requestServiceRepository.getServiceByCarOwner_UniqueId(carOwnerUniqueId);
        logger.info("Total requests found for CarOwner " + carOwnerUniqueId + ": " + requests.size());
        return mapper.toResponseList(requests);
    }

    @Override
    @Cacheable(value = "requestsByGarage", key = "#garageId")
    public List<ServiceRequestsResponseDTO> getRequestsByGarage(Long garageId) {
        logger.info("Fetching service requests for Garage with ID: " + garageId);
        List<ServiceRequest> requests = requestServiceRepository.getServiceByGarage_GarageId(garageId);
        logger.info("Total requests found for Garage " + garageId + ": " + requests.size());
        return mapper.toResponseList(requests);
    }

    @Override
    @Cacheable(value = "requestById", key = "#requestId")
    public Optional<ServiceRequestsResponseDTO> getRequestById(Long requestId) {
        logger.info("Fetching service request by ID: " + requestId);
        Optional<ServiceRequest> request = requestServiceRepository.findById(requestId);
        if (request.isPresent()) {
            logger.info("Service request found with ID: " + requestId);
        } else {
            logger.warning("No service request found with ID: " + requestId);
        }
        return mapper.toOptionalResponse(request);
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
