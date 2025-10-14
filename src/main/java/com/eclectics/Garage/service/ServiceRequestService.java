package com.eclectics.Garage.service;

import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.model.Status;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestService {

    ServiceRequest createRequest(Integer carOwnerUniqueId, Long garageId, Long serviceId, Long severityId);
    List<ServiceRequest> getAllRequests();
    ServiceRequest updateStatus(Long requestId, Status status, Long severityId);
    List<ServiceRequest> getRequestsByCarOwner(Integer carOwnerUniqueId);
    List<ServiceRequest> getRequestsByGarage(Long garageId);
    Optional<ServiceRequest> getRequestById(Long requestId);
    void deleteServiceRequest(Long id);
}

