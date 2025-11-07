package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.AutoMobileResponseDTO;
import com.eclectics.Garage.dto.AutomobileRequestsDTO;
import com.eclectics.Garage.model.AutoMobiles;
import com.eclectics.Garage.service.AutomobilesService;
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
    public List<AutoMobileResponseDTO> getAllAutomobiles(){
        return automobilesService.getAllAutomobiles();
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/make")
    public List<String> getAllMakes() {
        return automobilesService.getAllMakes();
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/year")
    public List<String> getAllYea() {
        return automobilesService.findAllYears();
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/engineType")
    public List<String> getAllEngineType() {
        return automobilesService.findAllEngineType();
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/transmission")
    public List<String> getTransmissions() {
        return automobilesService.findAllTransmission()
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping
    public AutoMobileResponseDTO createAutoMobile(@RequestBody AutomobileRequestsDTO automobileRequestsDTO){
        return automobilesService.createAutoMobile(automobileRequestsDTO);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public AutoMobileResponseDTO updateAutoMobile(@PathVariable Long id, @RequestBody AutomobileRequestsDTO autoMobiles){
        return automobilesService.updateAutoMobile(id, autoMobiles);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteAutoMobile(@PathVariable Long id){
        automobilesService.deleteAutoMobile(id);
    }
}
