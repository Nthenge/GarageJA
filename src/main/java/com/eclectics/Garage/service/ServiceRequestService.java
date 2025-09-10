package com.eclectics.Garage.service;

import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.Status;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestService {

    // Create a new request
    ServiceRequest createRequest(Long customerId, Long garageId, Long serviceId);

    // Update request status (e.g. PENDING → IN_PROGRESS → COMPLETED)
    ServiceRequest updateStatus(Long requestId, Status status);

    // Fetch requests for a given customer
    List<ServiceRequest> getRequestsByCustomer(Long customerId);

    // Fetch requests for a given garage
    List<ServiceRequest> getRequestsByGarage(Long garageId);

    // Fetch single request details
    Optional<ServiceRequest> getRequestById(Long requestId);
}

