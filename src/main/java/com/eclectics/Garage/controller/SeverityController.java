package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.SeverityRequestDTO;
import com.eclectics.Garage.dto.SeverityResponseDTO;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.SeverityCategoriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/severity")
public class SeverityController {

    SeverityCategoriesService severityCategoriesService;

    public SeverityController(SeverityCategoriesService severityCategoriesService) {
        this.severityCategoriesService = severityCategoriesService;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Object> createASeverity(@RequestBody SeverityRequestDTO severityRequestDTO){
         severityCategoriesService.createCategory(severityRequestDTO);
        return ResponseHandler.generateResponse( "Severity Category created", HttpStatus.CREATED, null, "/severity/create");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllSeverityCategories(){
        List<SeverityResponseDTO> allSeverityCategories = severityCategoriesService.getAllSeverityCategories();
        return ResponseHandler.generateResponse("All severity Categories", HttpStatus.OK, allSeverityCategories, "/severity");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @GetMapping("/{severityName}")
    public ResponseEntity<Object> getSeverityCategoryByName(@PathVariable String severityName){
        SeverityResponseDTO severityResponseDTO = severityCategoriesService.getSeverityCategoryByName(severityName);
        return ResponseHandler.generateResponse("All severity Categories", HttpStatus.OK, severityResponseDTO, "/severity");
    }
}
