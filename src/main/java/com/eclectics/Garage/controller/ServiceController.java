package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.service.ServicesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")
public class ServiceController {

        ServicesService servicesService;

        public ServiceController(ServicesService servicesService) {
            this.servicesService = servicesService;
        }


        @GetMapping("/{serviceId}")
        public Optional<ServiceResponseDTO> getOneService(@PathVariable("serviceId") Long Id){
            return servicesService.getServiceById(Id);
        }

        @GetMapping("/search/{garageId}")
        public List<ServiceResponseDTO> getAllServicesByGarageId(@PathVariable("garageId") Long garageId){
            return servicesService.getServicesByGarageId(garageId);
        }

        @GetMapping()
        public List<ServiceResponseDTO> getAllServices(){
            return servicesService.getAllServices();
        }

//        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @PostMapping()
        public String createService(@RequestBody ServiceRequestDTO serviceRequestDTO){
            servicesService.createService(serviceRequestDTO);
            return "Service created successfully";
        }

//        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @PutMapping("/{serviceId}")
        public String updateService(@PathVariable Long serviceId, @RequestBody ServiceRequestDTO serviceRequestDTO){
            servicesService.updateService(serviceId, serviceRequestDTO);
            return "Service updated successfully";
        }

//        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @DeleteMapping("/{serviceId}")
        public String deleteAService(@PathVariable("serviceId") Long serviceId){
            servicesService.deleteService(serviceId);
            return "Service Deleted Successfully";
        }
}
