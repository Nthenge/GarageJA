package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.RequestStatus;
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

    @PutMapping("/status/{requestId}")
    public ServiceRequest updateRequest(
            @PathVariable Long requestId,
            @RequestParam RequestStatus status,
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

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id){
        serviceRequestService.deleteServiceRequest(id);
        return "Service request deleted";
    }
}
