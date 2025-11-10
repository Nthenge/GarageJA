package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.RequestStatus;
import com.eclectics.Garage.service.ServiceRequestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/request")
public class ServiceRequestsController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestsController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping
    public List<ServiceRequestsResponseDTO> getAllReguests(){
        return serviceRequestService.getAllRequests();
    }
    @PreAuthorize("hasRole('CAR_OWNER')")
    @PostMapping
    public ServiceRequest createRequest(
            @RequestParam Long garageId,
            @RequestParam Long serviceId,
            @RequestParam Long severityId
            ){
        return serviceRequestService.createRequest(garageId, serviceId, severityId);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN')")
    @PutMapping("/status/{requestId}")
    public ServiceRequestsResponseDTO updateRequest(
            @PathVariable Long requestId,
            @RequestParam RequestStatus status,
            @RequestParam Long severityId,
            @RequestBody ServiceRequestsRequestDTO serviceRequestsRequestDTO
            ){
        return serviceRequestService.updateStatus(requestId, status,severityId, serviceRequestsRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER')")
    @GetMapping("/carOwner/{carOwnerUniqueId}")
    public List<ServiceRequestsResponseDTO> getRequestsByCarOwner(@PathVariable Integer carOwnerUniqueId){
        return serviceRequestService.getRequestsByCarOwner(carOwnerUniqueId);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @GetMapping("/garage/{garageId}")
    public List<ServiceRequestsResponseDTO> getRequestsByGarage(@PathVariable Long garageId){
        return serviceRequestService.getRequestsByGarage(garageId);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @GetMapping("/{requestId}")
    public Optional<ServiceRequestsResponseDTO> getRequestById(@PathVariable Long requestId){
        return serviceRequestService.getRequestById(requestId);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id){
        serviceRequestService.deleteServiceRequest(id);
        return "Service request deleted";
    }
}
