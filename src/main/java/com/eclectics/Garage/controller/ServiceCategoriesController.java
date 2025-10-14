package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.service.ServiceCategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class ServiceCategoriesController {
    ServiceCategoriesService serviceCategoriesService;

    public ServiceCategoriesController(ServiceCategoriesService serviceCategoriesService) {
        this.serviceCategoriesService = serviceCategoriesService;
    }

    @PostMapping
    public ResponseEntity<ServiceCategories> createServiceCategories(@RequestBody ServiceCategories serviceCategories){
        return ResponseEntity.ok(serviceCategoriesService.createCategory(serviceCategories));
    }

    @GetMapping
    public ResponseEntity<List<ServiceCategories>> getAllServiceCategories(){
        return ResponseEntity.ok(serviceCategoriesService.getAllServiceCategories());
    }

    @GetMapping("/{serviceCategoryName}")
    public ResponseEntity<ServiceCategories> getServiceCategoryByName(@PathVariable("serviceCategoryName") String serviceCategoryName){
        return ResponseEntity.ok(serviceCategoriesService.getServiceCategoryByName(serviceCategoryName));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceCategory(@PathVariable Long id){
        serviceCategoriesService.delete(id);
        return ResponseEntity.ok("Service category deleted");
    }
}
