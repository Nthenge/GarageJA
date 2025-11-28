package com.eclectics.Garage.controller;
import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.exception.GarageExceptions;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.GarageService;
import com.eclectics.Garage.service.ServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/garage")
public class GarageController {

    private final GarageService garageService;
    private final GarageRepository garageRepository;
    private final ServiceRepository serviceRepository;
    private final ServicesService servicesService;

    public GarageController(GarageService garageService, GarageRepository garageRepository, ServiceRepository serviceRepository, ServicesService servicesService) {
        this.garageService = garageService;
        this.garageRepository = garageRepository;
        this.serviceRepository = serviceRepository;
        this.servicesService = servicesService;
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN')")
    @PostMapping("/{garageId}/choose-services")
    public ResponseEntity<Object> assignServicesToGarage(
            @PathVariable Long garageId,
            @RequestBody List<Long> serviceIds) {

        servicesService.assignMultipleServicesToGarage(garageId, serviceIds);

        return ResponseHandler.generateResponse(
                "Services assigned to garage successfully", HttpStatus.CREATED, null, "/garage/{garageId}/choose-services");
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN')")
    @PostMapping("/{garageId}/choose-service/{serviceId}")
    public ResponseEntity<Object> assignServiceToGarage(
            @PathVariable Long garageId,
            @PathVariable Long serviceId) {

        ServiceResponseDTO assignedService = servicesService.assignServiceToGarage(serviceId, garageId);
        return ResponseHandler.generateResponse("Service assinged to garage", HttpStatus.OK, assignedService, "/garage{garageId}/choose-service/{serviceId}");
    }

    @GetMapping("/garage/{garageId}/services")
    public ResponseEntity<Object> getServicesByGarage(@PathVariable Long garageId) {
        Garage garage = garageRepository.findByGarageId(garageId)
                .orElseThrow(() -> new GarageExceptions.ResourceNotFoundException("Garage not found"));

        return ResponseHandler.generateResponse(
                "Garage services", HttpStatus.OK, garage.getOfferedServices(), "/garage/garage/{garageId}/services");
    }

    @PostMapping

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'MECHANIC', 'CAR_OWNER')")
    @GetMapping("/count")
    public ResponseEntity<Object> getGarageCount() {
        long count = garageService.countAllGarages();
        return ResponseHandler.generateResponse("Count of garages", HttpStatus.OK, count, "/garage/count");
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN','CAR_OWNER')")
    @GetMapping("/search")
    public ResponseEntity<Object> searchGarages(
            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) String physicalBusinessAddress
    ) {
        List<GarageResponseDTO> garages = garageService.filterGarages(businessName, physicalBusinessAddress);
        return ResponseHandler.generateResponse("Filtered garages", HttpStatus.OK, garages, "/garage/search");
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createGarage(
            @RequestPart("garage") GarageRequestsDTO garageRequestsDTO,
            @RequestPart(value = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestPart(value = "professionalCertificate", required = false) MultipartFile professionalCertificate,
            @RequestPart(value = "facilityPhotos", required = false) MultipartFile facilityPhotos) throws java.io.IOException{
        try{
            garageService.createGarage(garageRequestsDTO, businessLicense, professionalCertificate, facilityPhotos);
            return ResponseHandler.generateResponse("Garage created successfully", HttpStatus.CREATED,null,"/garage/create" );
        }catch (java.io.IOException e){
            return ResponseHandler.generateResponse("Error creating garage: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,null,"/garage/create" );
        }
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<Object> updateGarage(
            @ModelAttribute("garage") GarageRequestsDTO garageRequestsDTO,
            @RequestPart(value = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestPart(value = "professionalCertificate", required = false) MultipartFile professionalCertificate,
            @RequestPart(value = "facilityPhotos", required = false) MultipartFile facilityPhotos){
        garageService.updateOwnGarage(garageRequestsDTO, businessLicense, professionalCertificate, facilityPhotos);
        return ResponseHandler.generateResponse("Garage updated successfully", HttpStatus.CREATED, null, "/garage/update");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @DeleteMapping("/{garageId}")
    public ResponseEntity<Object> deleteAGarage(@PathVariable("garageId") Long garageId){
        garageService.deleteGarage(garageId);
        return ResponseHandler.generateResponse("Garage Deleted Successfully", HttpStatus.OK, null,"/garage/{garageId}" );
    }

}

