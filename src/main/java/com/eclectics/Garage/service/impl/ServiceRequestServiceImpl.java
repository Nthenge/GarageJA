package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.CustomerRepository;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.RequestServiceRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServiceRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final RequestServiceRepository requestServiceRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;

    public ServiceRequestServiceImpl(RequestServiceRepository requestServiceRepository, CustomerRepository customerRepository, ServiceRepository serviceRepository, GarageRepository garageRepository) {
        this.requestServiceRepository = requestServiceRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
    }

    @Override
    public ServiceRequest createRequest(Long customerId, Long garageId, Long serviceId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer with this id does not exist"));

        Garage garage = garageRepository.findById(garageId)
                .orElseThrow(() -> new RuntimeException("Garage with this id does not exist"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service with this id does not exist"));

        ServiceRequest request = new ServiceRequest();
        request.setCustomer(customer);
        request.setGarage(garage);
        request.setService(service);
        request.setStatus(Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return requestServiceRepository.save(request);
    }

    @Override
    public ServiceRequest updateStatus(Long requestId, Status status) {
        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Service with this id " + requestId + " not found"));

        serviceRequest.setStatus(status);
        serviceRequest.setUpdatedAt(LocalDateTime.now());
        return requestServiceRepository.save(serviceRequest);
    }

    @Override
    public List<ServiceRequest> getRequestsByCustomer(Long customerId) {
        return requestServiceRepository.getServiceByCustomerId(customerId);
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
