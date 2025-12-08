package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.MechanicLocationUpdateDTO;
import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.MechanicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mechanic")
public class MechanicController {

    MechanicService mechanicService;

    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    private ResponseEntity<Map<String, String>> success(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','CAR_OWNER')")
    @GetMapping("/search")
    public ResponseEntity<Object> filterMechanics(
            @RequestParam(required = false) String vehicleBrands,
            @RequestParam(required = false) Integer nationalIdNumber,
            @RequestParam(required = false) Long garageId
    ) {

        List<MechanicResponseDTO> mechanics = mechanicService.filterMechanics(
                vehicleBrands,
                nationalIdNumber,
                garageId
        );

        return ResponseHandler.generateResponse(
                "Mechanics fetched successfully",
                HttpStatus.OK,
                mechanics,
                "/mechanic/search"
        );
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','MECHANIC')")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateOwnMechanic(
            @RequestPart MechanicRequestDTO mechanicRequestDTO,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestPart(value = "nationalIDPic", required = true) MultipartFile nationalIDPic,
            @RequestPart(value = "professionalCertificate", required = false) MultipartFile professionalCertificate,
            @RequestPart(value = "anyRelevantCertificate", required = false) MultipartFile anyRelevantCertificate,
            @RequestPart(value = "policeClearanceCertificate", required = true) MultipartFile policeClearanceCertificate) {

        MechanicResponseDTO updatedMechanic = mechanicService.updateOwnMechanic(
                mechanicRequestDTO,
                profilePic,
                nationalIDPic,
                professionalCertificate,
                anyRelevantCertificate,
                policeClearanceCertificate);

        return ResponseHandler.generateResponse("Mechanic updated", HttpStatus.CREATED, updatedMechanic,"/mechanic/update" );
    }

    @PostMapping("/{mechanicId}/live-location")
    public ResponseEntity<Object> updateLocation(@PathVariable Long mechanicId,
                                                   @RequestBody @Valid MechanicLocationUpdateDTO locationDto) {
        Mechanic updatedMechanic = mechanicService.updateMechanicLiveLocation(mechanicId, locationDto);
        return ResponseHandler.generateResponse("Live location",HttpStatus.OK, updatedMechanic, "/{mechanicId}/live-location");
    }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'MECHANIC')")
    @DeleteMapping("/{MechanicId}")
    public ResponseEntity<Object> deleteMechanic(@PathVariable("MechanicId") Long MechanicId){
        mechanicService.deleteMechanic(MechanicId);
        return ResponseHandler.generateResponse( "Mechanic Deleted Successfully", HttpStatus.OK, null, "/mechanic/{MechanicId}");
    }
}
