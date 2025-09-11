package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.service.ServicesService;
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
        public Optional<Service> getOneService(@PathVariable("serviceId") Long Id){
            return servicesService.getServiceById(Id);
        }

        @GetMapping("/search/{garageId}")
        public List<Service> getAllServicesByGarageId(@PathVariable("garageId") Long garageId){
            return servicesService.getServicesByGarageId(garageId);
    }

        @GetMapping()
        public List<Service> getAllServices(){
            return servicesService.getAllServices();
        }

        @PostMapping()
        public String createService(@RequestBody Service service){
            servicesService.createService(service);
            return "Service created successfully";
        }

        @PutMapping("/{serviceId}")
        public String updateCustomer(@PathVariable Long serviceId, @RequestBody Service service){
            servicesService.updateService(serviceId, service);
            return "Service updated successfully";
        }

        @DeleteMapping("/{serviceId}")
        public String deleteAService(@PathVariable("serviceId") Long serviceId){
            servicesService.deleteService(serviceId);
            return "Service Deleted Succesfully";
        }
}
