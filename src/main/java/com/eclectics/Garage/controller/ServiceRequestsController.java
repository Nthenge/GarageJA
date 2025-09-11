package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.Status;
import com.eclectics.Garage.service.ServiceRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service requests")
public class ServiceRequestsController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestsController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public ServiceRequest createRequest(
            @RequestParam Long carOwnerId,
            @RequestParam Long garageId,
            @RequestParam Long serviceId
            ){
        return serviceRequestService. createRequest(carOwnerId, garageId,serviceId);
    }

    @PutMapping("/{requestId}/status")
    public ServiceRequest updateRequest(
            @PathVariable Long requestId,
            @RequestParam Status status
            ){
        return serviceRequestService.updateStatus(requestId, status);
    }

    @GetMapping("/carOwner/{carOwnerUniqueId}")
    public List<ServiceRequest> getRequestsByCarOwner(@PathVariable Long carOwnerUniqueId){
        return serviceRequestService.getRequestsByCarOwner(carOwnerUniqueId);
    }

    @GetMapping("/garage/{garageId}")
    public List<ServiceRequest> getRequestsByGarage(@PathVariable Long garageId){
        return serviceRequestService.getRequestsByGarage(garageId);
    }

    @GetMapping("/{requestId}")
    public Optional<ServiceRequest> getRequestById(@PathVariable Long requestId){
        return serviceRequestService.getRequestById(requestId);
    }
}
