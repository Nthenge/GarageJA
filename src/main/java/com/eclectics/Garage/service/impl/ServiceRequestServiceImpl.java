package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.*;
import com.eclectics.Garage.service.ServiceRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final RequestServiceRepository requestServiceRepository;
    private final CarOwnerRepository carOwnerRepository;
    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;
    private final SeverityCategoryRepository severityCategoryRepository;

    public ServiceRequestServiceImpl(RequestServiceRepository requestServiceRepository,SeverityCategoryRepository severityCategoryRepository, CarOwnerRepository carOwnerRepository, ServiceRepository serviceRepository, GarageRepository garageRepository) {
        this.requestServiceRepository = requestServiceRepository;
        this.carOwnerRepository = carOwnerRepository;
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
        this.severityCategoryRepository = severityCategoryRepository;
    }

    @Override
    public ServiceRequest createRequest(Integer carOwnerUniqueId, Long garageId, Long serviceId, Long severityId) {
        CarOwner carOwner = carOwnerRepository.findByUniqueId(carOwnerUniqueId)
                .orElseThrow(() -> new RuntimeException("Car Owner with this id does not exist"));

        Garage garage = garageRepository.findById(garageId)
                .orElseThrow(() -> new RuntimeException("Garage with this id does not exist"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service with this id does not exist"));

        SeverityCategories severityCategory = severityCategoryRepository.findById(severityId)
                .orElseThrow(()-> new RuntimeException("Severity with this id, does not exist"));

        ServiceRequest request = new ServiceRequest();
        request.setCarOwner(carOwner);
        request.setGarage(garage);
        request.setService(service);
        request.setStatus(Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setSeverityCategories(severityCategory);
        return requestServiceRepository.save(request);
    }

    @Override
    public List<ServiceRequest> getAllRequests() {
        return requestServiceRepository.findAll();
    }

    @Override
    public ServiceRequest updateStatus(Long requestId, Status status, Long severityId) {
     ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Service with this id " + requestId + " not found"));

        if(serviceRequest.getStatus() != null) serviceRequest.setStatus(status);
        if (serviceRequest.getUpdatedAt() != null) serviceRequest.setUpdatedAt(LocalDateTime.now());
        if (serviceRequest.getSeverityCategories() != null) serviceRequest.setSeverityCategories(serviceRequest.getSeverityCategories());
        return requestServiceRepository.save(serviceRequest);
    }

    @Override
    public List<ServiceRequest> getRequestsByCarOwner(Long carOwnerUniqueId) {
        return requestServiceRepository.getServiceByCarOwnerId(carOwnerUniqueId);
    }

    @Override
    public List<ServiceRequest> getRequestsByGarage(Long garageId) {
        return requestServiceRepository.getServiceByGarageId(garageId);
    }

    @Override
    public Optional<ServiceRequest> getRequestById(Long requestId) {
        return requestServiceRepository.findById(requestId);
    }
}
