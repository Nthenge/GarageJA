package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.mapper.ServiceRequestsMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.*;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.ServiceRequestService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.time.LocalDateTime;
import java.util.*;
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
    private final AuthenticationService authenticationService;

    private final Map<Long, ServiceRequest> requestCache = new HashMap<>();
    private final Map<Integer, List<ServiceRequest>> requestsByCarOwnerCache = new HashMap<>();
    private final Queue<ServiceRequest> recentRequestsQueue = new LinkedList<>();
    private final Set<Long> deletedRequestIds = new HashSet<>();

    public ServiceRequestServiceImpl(
            RequestServiceRepository requestServiceRepository,
            SeverityCategoryRepository severityCategoryRepository,
            CarOwnerRepository carOwnerRepository,
            ServiceRepository serviceRepository,
            GarageRepository garageRepository,
            ServiceRequestsMapper mapper, AuthenticationService authenticationService
    ) {
        this.requestServiceRepository = requestServiceRepository;
        this.carOwnerRepository = carOwnerRepository;
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.severityCategoryRepository = severityCategoryRepository;
        this.mapper = mapper;
        this.authenticationService = authenticationService;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", allEntries = true)
    })
    public ServiceRequest createRequest(Long garageId, Long serviceId, Long severityId) {

        User currentUser = authenticationService.getCurrentUser();
        CarOwner carOwner = carOwnerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Car Owner not found for current user"));

        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        SeverityCategories severityCategory = severityCategoryRepository.findById(severityId)
                .orElseThrow(() -> new ResourceNotFoundException("Severity not found"));

        ServiceRequest request = new ServiceRequest();
        request.setCarOwner(carOwner);
        request.setGarage(garage);
        request.setService(service);
        request.setSeverityCategories(severityCategory);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        ServiceRequest savedRequest = requestServiceRepository.save(request);

        requestCache.put(savedRequest.getId(), savedRequest);
        requestsByCarOwnerCache.computeIfAbsent(carOwner.getUniqueId(), k -> new ArrayList<>()).add(savedRequest);
        if (recentRequestsQueue.size() > 20) recentRequestsQueue.poll();
        recentRequestsQueue.offer(savedRequest);

        return savedRequest;
    }


    @Override
    @Cacheable(value = "allServiceRequests")
    public List<ServiceRequestsResponseDTO> getAllRequests() {
        logger.info("Fetching all service requests...");

        if (!requestCache.isEmpty()) {
            logger.info("Returning cached requests: " + requestCache.size());
            return mapper.toResponseList(new ArrayList<>(requestCache.values()));
        }

        List<ServiceRequest> requests = requestServiceRepository.findAll();
        logger.info("Total service requests fetched: " + requests.size());

        for (ServiceRequest req : requests) {
            requestCache.put(req.getId(), req);
        }

        return mapper.toResponseList(requests);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allServiceRequests", allEntries = true),
            @CacheEvict(value = "requestsByGarage", allEntries = true),
            @CacheEvict(value = "requestsByCarOwner", allEntries = true),
            @CacheEvict(value = "requestById", key = "#requestId")
    })
    public ServiceRequestsResponseDTO updateStatus(Long requestId, RequestStatus status) {

        logger.info(String.format("Updating status for request ID: %d to %s", requestId, status));

        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.warning("Service request with ID " + requestId + " not found.");
                    return new ResourceNotFoundException("Service with this id " + requestId + " not found");
                });

        if (status != null) serviceRequest.setStatus(status);
        serviceRequest.setUpdatedAt(LocalDateTime.now());

//        if (severityId != null) {
//            severityCategoryRepository.findById(severityId).ifPresentOrElse(
//                    serviceRequest::setSeverityCategories,
//                    () -> logger.warning("Severity category with ID " + severityId + " not found. Keeping previous severity.")
//            );
//        }

        ServiceRequest updatedRequest = requestServiceRepository.save(serviceRequest);
        logger.info("Service request updated successfully with ID: " + requestId);

        requestCache.put(requestId, updatedRequest);
        return mapper.toResponse(updatedRequest);
    }

    @Override
    @Cacheable(value = "requestsByCarOwner", key = "#carOwnerUniqueId")
    public List<ServiceRequestsResponseDTO> getRequestsByCarOwner(Integer carOwnerUniqueId) {
        logger.info("Fetching service requests for CarOwner with ID: " + carOwnerUniqueId);

        if (requestsByCarOwnerCache.containsKey(carOwnerUniqueId)) {
            logger.info("Returning cached requests for CarOwner: " + carOwnerUniqueId);
            return mapper.toResponseList(requestsByCarOwnerCache.get(carOwnerUniqueId));
        }

        List<ServiceRequest> requests = requestServiceRepository.getServiceByCarOwner_UniqueId(carOwnerUniqueId);
        logger.info("Total requests found for CarOwner " + carOwnerUniqueId + ": " + requests.size());
        requestsByCarOwnerCache.put(carOwnerUniqueId, requests);

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

        if (requestCache.containsKey(requestId)) {
            logger.info("Returning cached request for ID: " + requestId);
            return Optional.of(mapper.toResponse(requestCache.get(requestId)));
        }

        Optional<ServiceRequest> request = requestServiceRepository.findById(requestId);
        request.ifPresent(r -> requestCache.put(r.getId(), r));

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

            requestCache.remove(id);
            deletedRequestIds.add(id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting service request with ID: " + id, e);
            throw e;
        }
    }
}
