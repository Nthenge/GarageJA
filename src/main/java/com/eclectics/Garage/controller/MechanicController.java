package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.service.MechanicService;
import io.jsonwebtoken.io.IOException;
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
        public Optional<Mechanic> getMechanicByNationalId(@PathVariable("nationalIdNumber") Integer nationalIdNumber){
            return mechanicService.getMechanicByNationalId(nationalIdNumber);
        }

        @GetMapping()
        public List<Mechanic> getAllMechanics(){
            return mechanicService.getAllMechanics();
        }

        @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> createMechanic( @RequestBody Mechanic mechanic){
            mechanicService.createMechanic(mechanic);
            return ResponseEntity.ok("Mechanic created successfully");
        }

        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<String> uploadDocuments(
                @RequestParam("id") Long id,
                @RequestPart(value = "profilepic", required = false)MultipartFile profilepic,
                @RequestPart(value = "nationalIdFile", required = false) MultipartFile nationalIdFile,
                @RequestPart(value = "profCert", required = false)MultipartFile profCert,
                @RequestPart(value = "anyRelCert", required = false)MultipartFile anyRelCert,
                @RequestPart(value = "polCleCert", required = false)MultipartFile polCleCert) throws IOException, java.io.IOException {
            mechanicService.uploadDocuments(id, profilepic, nationalIdFile, profCert, anyRelCert, polCleCert);
            return ResponseEntity.ok("Mechanic created successfully");
        }

        @PutMapping("/{mechanicId}")
        public String updateMechanic(@PathVariable Long mechanicId, @RequestBody Mechanic mechanic){
            mechanicService.updateMechanic(mechanicId, mechanic);
            return "Mechanic updated successfully";
        }

        @DeleteMapping("/{MechanicId}")
        public String deleteAGarage(@PathVariable("MechanicId") Long MechanicId){
            mechanicService.deleteMechanic(MechanicId);
            return "Mechanic Deleted Succesfully";
        }
}
