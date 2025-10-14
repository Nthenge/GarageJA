package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.service.ServiceCategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class ServiceCategoriesController {
    ServiceCategoriesService serviceCategoriesService;

    public ServiceCategoriesController(ServiceCategoriesService serviceCategoriesService) {
        this.serviceCategoriesService = serviceCategoriesService;
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<ServiceCategories> createServiceCategories(@RequestBody ServiceCategories serviceCategories){
        return ResponseEntity.ok(serviceCategoriesService.createCategory(serviceCategories));
    }

    @GetMapping
    public ResponseEntity<List<ServiceCategories>> getAllServiceCategories(){
        return ResponseEntity.ok(serviceCategoriesService.getAllServiceCategories());
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC','CAR_OWNER')")
    @GetMapping("/{serviceCategoryName}")
    public ResponseEntity<ServiceCategories> getServiceCategoryByName(@PathVariable("serviceCategoryName") String serviceCategoryName){
        return ResponseEntity.ok(serviceCategoriesService.getServiceCategoryByName(serviceCategoryName));
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceCategory(@PathVariable Long id){
        serviceCategoriesService.delete(id);
        return ResponseEntity.ok("Service category deleted");
    }
}
