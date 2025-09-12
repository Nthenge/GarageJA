package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.SeverityCategories;
import com.eclectics.Garage.service.SeverityCategoriesService;
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

    @PostMapping()
    public String createASeverity(@RequestBody SeverityCategories severityCategories){
        severityCategoriesService.createCategory(severityCategories);
        return "Severity Category created";
    }

    @GetMapping
    public List<SeverityCategories> getAllSeverityCategories(){
        return severityCategoriesService.getAllSeverityCategories();
    }

    @GetMapping("/{severityName}")
    public SeverityCategories getSeverityCategoryByName(@PathVariable String severityName){
        return severityCategoriesService.getSeverityCategoryByName(severityName);
    }
}
