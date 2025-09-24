package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.service.MechanicService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mechanic")
public class MechanicController {

        MechanicService mechanicService;

        public MechanicController(MechanicService mechanicService) {
            this.mechanicService = mechanicService;
        }

        @GetMapping("/search/{nationalIdNumber}")
        public Optional<MechanicResponseDTO> getMechanicByNationalId(@PathVariable("nationalIdNumber") Integer nationalIdNumber){
            return mechanicService.getMechanicByNationalId(nationalIdNumber);
        }

        @GetMapping()
        public List<MechanicResponseDTO> getAllMechanics(){
            return mechanicService.getAllMechanics();
        }

        @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_OCTET_STREAM_VALUE})
        public ResponseEntity<String> createMechanic(
                @RequestPart("mechanic") MechanicRequestDTO mechanicRequestDTO,
                @RequestPart(value = "profilepic", required = false)MultipartFile profilepic,
                @RequestPart(value = "nationalIdFile", required = true) MultipartFile nationalIdFile,
                @RequestPart(value = "profCert", required = false)MultipartFile profCert,
                @RequestPart(value = "anyRelCert", required = false)MultipartFile anyRelCert,
                @RequestPart(value = "polCleCert", required = true)MultipartFile polCleCert) throws java.io.IOException{
            mechanicService.createMechanic(mechanicRequestDTO,profilepic,nationalIdFile,profCert,anyRelCert,polCleCert);
            return ResponseEntity.ok("Mechanic created successfully");
        }

        @PutMapping("/{mechanicId}")
        public ResponseEntity<MechanicResponseDTO> updateMechanic(@PathVariable Long mechanicId, @RequestBody MechanicRequestDTO mechanicRequestDTO){
            MechanicResponseDTO updatedMechanic = mechanicService.updateMechanic(mechanicId, mechanicRequestDTO);
            return ResponseEntity.ok(updatedMechanic);
        }

        @DeleteMapping("/{MechanicId}")
        public String deleteAGarage(@PathVariable("MechanicId") Long MechanicId){
            mechanicService.deleteMechanic(MechanicId);
            return "Mechanic Deleted Succesfully";
        }
}
