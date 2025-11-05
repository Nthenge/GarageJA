package com.eclectics.Garage.controller;
import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.service.GarageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/garage")
public class GarageController {

    GarageService garageService;

    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getGarageCount() {
        long count = garageService.countAllGarages();
        return ResponseEntity.ok(count);
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'MECHANIC')")
    @GetMapping("/search/{garageId}")
    public ResponseEntity<GarageResponseDTO> getGarageById(@PathVariable Long garageId){
        Optional<GarageResponseDTO> garage = garageService.getGarageById(garageId);
        if (garage.isPresent()){
            return ResponseEntity.ok(garage.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN','CAR_OWNER')")
    @GetMapping()
    public List<GarageResponseDTO> getAllGarages(){
        return garageService.getAllGarages();
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String createGarage(
            @RequestPart("garage") GarageRequestsDTO garageRequestsDTO,
            @RequestPart(value = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestPart(value = "professionalCertificate", required = false) MultipartFile professionalCertificate,
            @RequestPart(value = "facilityPhotos", required = false) MultipartFile facilityPhotos) throws java.io.IOException{
        try{
            garageService.createGarage(garageRequestsDTO, businessLicense, professionalCertificate, facilityPhotos);
            return "Garage created successfully";
        }catch (java.io.IOException e){
            return "Error creating garage: " + e.getMessage();
        }
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @PutMapping("/{garageId}")
    public String updateGarage(
            @PathVariable Long garageId,
            @RequestPart("garage") GarageRequestsDTO garageRequestsDTO,
            @RequestPart(value = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestPart(value = "professionalCertificate", required = false) MultipartFile professionalCertificate,
            @RequestPart(value = "facilityPhotos", required = false) MultipartFile facilityPhotos){
        garageService.updateGarage(garageId, garageRequestsDTO,businessLicense,professionalCertificate,facilityPhotos);
        return "Garage updated successfully";
    }

//    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
    @DeleteMapping("/{garageId}")
    public String deleteAGarage(@PathVariable("garageId") Long garageId){
        garageService.deleteGarage(garageId);
        return "Garage Deleted Successfully";
    }

}

