package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.ServiceRequestsRequestDTO;
import com.eclectics.Garage.dto.ServiceRequestsResponseDTO;
import com.eclectics.Garage.model.ServiceRequest;
import com.eclectics.Garage.model.RequestStatus;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.ServiceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//    @PreAuthorize("hasAnyAuthority('CAR_OWNER')")
    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestParam Long garageId,
            @RequestParam Long serviceId,
            @RequestParam Long severityId
            ){
        ServiceRequest serviceRequest = serviceRequestService.createRequest(garageId, serviceId, severityId);
        return ResponseHandler.generateResponse("Request send", HttpStatus.CREATED, serviceRequest);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN')")
    @PutMapping("/update/{requestId}")
    public ResponseEntity<Object> updateRequest(
            @PathVariable Long requestId,
            @RequestParam RequestStatus status
            ){

        ServiceRequestsResponseDTO serviceRequestsResponseDTO = serviceRequestService.updateStatus(requestId, status);
        return ResponseHandler.generateResponse("Update request", HttpStatus.OK, serviceRequestsResponseDTO);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER')")
    @GetMapping("/carOwner/{carOwnerUniqueId}")
    public ResponseEntity<Object> getRequestsByCarOwner(@PathVariable Integer carOwnerUniqueId){
        List<ServiceRequestsResponseDTO> requestsByCarOwner = serviceRequestService.getRequestsByCarOwner(carOwnerUniqueId);
        return ResponseHandler.generateResponse("Request By CarOwner", HttpStatus.OK, carOwnerUniqueId);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @GetMapping("/garage/{garageId}")
    public ResponseEntity<Object> getRequestsByGarage(@PathVariable Long garageId){
        List<ServiceRequestsResponseDTO> requestByGarage = serviceRequestService.getRequestsByGarage(garageId);
        return ResponseHandler.generateResponse("Request By Garage", HttpStatus.OK, requestByGarage);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId){
        Optional<ServiceRequestsResponseDTO> requestById = serviceRequestService.getRequestById(requestId);
        return ResponseHandler.generateResponse("Request By id", HttpStatus.OK, requestById);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id){
        serviceRequestService.deleteServiceRequest(id);
        return ResponseHandler.generateResponse( "Service request deleted", HttpStatus.OK, null);
    }
}
