package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.service.MechanicService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN')")
        @GetMapping("/search/{nationalIdNumber}")
        public Optional<MechanicResponseDTO> getMechanicByNationalId(@PathVariable("nationalIdNumber") Integer nationalIdNumber){
            return mechanicService.getMechanicByNationalId(nationalIdNumber);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @GetMapping()
        public List<MechanicResponseDTO> getAllMechanics(){
            return mechanicService.getAllMechanics();
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'MECHANIC')")
        @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_OCTET_STREAM_VALUE})
        public ResponseEntity<String> createMechanic(
                @RequestPart("mechanic") MechanicRequestDTO mechanicRequestDTO,
                @RequestPart(value = "profilePic", required = false)MultipartFile profilePic,
                @RequestPart(value = "nationalIDPic", required = true) MultipartFile nationalIDPic,
                @RequestPart(value = "professionalCertfificate", required = false)MultipartFile professionalCertfificate,
                @RequestPart(value = "anyRelevantCertificate", required = false)MultipartFile anyRelevantCertificate,
                @RequestPart(value = "policeClearanceCertficate", required = true)MultipartFile policeClearanceCertficate) throws java.io.IOException{
            mechanicService.createMechanic(mechanicRequestDTO,profilePic,nationalIDPic,professionalCertfificate,anyRelevantCertificate,policeClearanceCertficate);
            return ResponseEntity.ok("Mechanic created successfully");
        }

        @PreAuthorize("hasAuthority('MECHANIC')")
        @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<MechanicResponseDTO> updateOwnMechanic(
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

            return ResponseEntity.ok(updatedMechanic);
        }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'MECHANIC')")
        @DeleteMapping("/{MechanicId}")
        public String deleteMechanic(@PathVariable("MechanicId") Long MechanicId){
            mechanicService.deleteMechanic(MechanicId);
            return "Mechanic Deleted Successfully";
        }
}
