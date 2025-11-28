package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.ServiceCategoriesResponseDTO;
import com.eclectics.Garage.dto.ServiceCategoriestRequestDTO;
import com.eclectics.Garage.model.ServiceCategories;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.ServiceCategoriesService;
import org.springframework.http.HttpStatus;
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

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Object> createServiceCategories(@RequestBody ServiceCategoriestRequestDTO serviceCategoriestRequestDTO){
        ServiceCategories serviceCategories =serviceCategoriesService.createCategory(serviceCategoriestRequestDTO);
        return ResponseHandler.generateResponse("Service created", HttpStatus.CREATED, serviceCategories, "/category/create");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'GARAGE_ADMIN', 'CAR_OWNER', 'MECHANIC')")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllServiceCategories(){
        List<ServiceCategoriesResponseDTO> allservices = serviceCategoriesService.getAllServiceCategories();
        return ResponseHandler.generateResponse("All services", HttpStatus.OK, allservices, "/category/all");
    }

//update/{categoryId}

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC','CAR_OWNER')")
    @GetMapping("/{serviceCategoryName}")
    public ResponseEntity<Object> getServiceCategoryByName(@PathVariable("serviceCategoryName") String serviceCategoryName){
        ServiceCategoriesResponseDTO serviceCategoriesResponseDTO = serviceCategoriesService.getServiceCategoryByName(serviceCategoryName);
        return ResponseHandler.generateResponse("Service Category by name", HttpStatus.OK, serviceCategoriesResponseDTO, "/category/{serviceCategoryName}");
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteServiceCategory(@PathVariable Long id){
        serviceCategoriesService.delete(id);
        return ResponseHandler.generateResponse("Service category deleted", HttpStatus.OK, null,"/category/delete/{id}" );
    }
}
