package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.AutomobilesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/automobile")
public class AutoMobilesController {

    AutomobilesService automobilesService;

    public AutoMobilesController(AutomobilesService automobilesService) {
        this.automobilesService = automobilesService;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping
    public ResponseEntity<Object> getAllAutomobiles(){
        List<AutoMobileResponseDTO> automobiles= automobilesService.getAllAutomobiles();
        return ResponseHandler.generateResponse("All engine Types", HttpStatus.OK, automobiles, "/automobile");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/make")
    public ResponseEntity<Object> getAllMakes() {
        List<String> makes= automobilesService.getAllMakes();
        return ResponseHandler.generateResponse("All engine Types", HttpStatus.OK, makes, "/automobile/make");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/year")
    public ResponseEntity<Object> getAllYear() {
        List<String> year= automobilesService.findAllYears();
        return ResponseHandler.generateResponse("All engine Types", HttpStatus.OK, year, "/automobile/year");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/engineType")
    public ResponseEntity<Object> getAllEngineType() {
        List<String> engineType= automobilesService.findAllEngineType();
        return ResponseHandler.generateResponse("All engine Types", HttpStatus.OK, engineType, "/automobile/engineType");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/transmission")
    public ResponseEntity<Object>getTransmissions() {
        List<String> transmission = automobilesService.findAllTransmission()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        return ResponseHandler.generateResponse("All Transmissions", HttpStatus.OK, transmission, "/automobile/transmission");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> createAutoMobile(@RequestBody AutomobileRequestsDTO automobileRequestsDTO){
        AutoMobileResponseDTO createdAutomobile = automobilesService.createAutoMobile(automobileRequestsDTO);
        return ResponseHandler.generateResponse("Automobile created", HttpStatus.CREATED, createdAutomobile, "/automobile");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public  ResponseEntity<Object>updateAutoMobile(@PathVariable Long id, @RequestBody AutomobileRequestsDTO autoMobiles){
        AutoMobileResponseDTO updatedAutomobile = automobilesService.updateAutoMobile(id, autoMobiles);
        return ResponseHandler.generateResponse("Updated Automobile", HttpStatus.CREATED, updatedAutomobile, "/automobile/{id}");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAutoMobile(@PathVariable Long id){
        automobilesService.deleteAutoMobile(id);
        return ResponseHandler.generateResponse("Automobile deleted", HttpStatus.OK, null, "/automobile/{id}");
    }
}
