package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.model.Status;
import com.eclectics.Garage.service.ServiceRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
public class ServiceRequestsController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestsController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping
    public List<ServiceRequest> getAllReguests(){
        return serviceRequestService.getAllRequests();
    }

    @PostMapping
    public ServiceRequest createRequest(
            @RequestParam Integer carOwnerId,
            @RequestParam Long garageId,
            @RequestParam Long serviceId,
            @RequestParam Long severityId
            ){
        return serviceRequestService.createRequest(carOwnerId, garageId, serviceId, severityId);
    }

    @PutMapping("/{requestId}/status")
    public ServiceRequest updateRequest(
            @PathVariable Long requestId,
            @RequestParam Status status,
            @RequestParam Long severityId
            ){
        return serviceRequestService.updateStatus(requestId, status,severityId);
    }

    @GetMapping("/carOwner/{carOwnerUniqueId}")
    public List<ServiceRequest> getRequestsByCarOwner(@PathVariable Integer carOwnerUniqueId){
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
