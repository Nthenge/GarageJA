package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestService {

    ServiceRequest createRequest(Long garageId, Long serviceId, Long severityId);
    List<ServiceRequestsResponseDTO> getAllRequests();
    ServiceRequestsResponseDTO updateStatus(Long requestId, RequestStatus status);
    List<ServiceRequestsResponseDTO> getRequestsByCarOwner(Integer carOwnerUniqueId);
    List<ServiceRequestsResponseDTO> getRequestsByGarage(Long garageId);
    Optional<ServiceRequestsResponseDTO> getRequestById(Long requestId);
    void deleteServiceRequest(Long id);
}

