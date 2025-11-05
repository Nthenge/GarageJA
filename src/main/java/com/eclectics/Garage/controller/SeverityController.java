package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.SeverityRequestDTO;
import com.eclectics.Garage.dto.SeverityResponseDTO;
import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.service.SeverityCategoriesService;
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

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping()
    public String createASeverity(@RequestBody SeverityRequestDTO severityRequestDTO){
        severityCategoriesService.createCategory(severityRequestDTO);
        return "Severity Category created";
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping
    public List<SeverityResponseDTO> getAllSeverityCategories(){
        return severityCategoriesService.getAllSeverityCategories();
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping("/{severityName}")
    public SeverityResponseDTO getSeverityCategoryByName(@PathVariable String severityName){
        return severityCategoriesService.getSeverityCategoryByName(severityName);
    }
}
