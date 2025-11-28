package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.ServiceRequestDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.ServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")
public class ServiceController {

        private final ServicesService servicesService;
        private final ResponseHandler responseHandler;

        public ServiceController(ServicesService servicesService, ResponseHandler responseHandler) {
            this.servicesService = servicesService;
            this.responseHandler = responseHandler;
        }

        @GetMapping("/singleservice/{serviceId}")
        public ResponseEntity<Object> getOneService(@PathVariable("serviceId") Long Id){
            Optional<ServiceResponseDTO> service = servicesService.getServiceById(Id);
            return ResponseHandler.generateResponse("Service by id", HttpStatus.OK, service, "/service/singleservice/{serviceId}");
        }

        @GetMapping("/search")
        public ResponseEntity<Object> searchServices(
                @RequestParam(required = false) String serviceName,
                @RequestParam(required = false) Double price,
                @RequestParam(required = false) String garageName
        ) {
            List<ServiceResponseDTO> services = servicesService.searchServices(serviceName, price, garageName);
            return ResponseHandler.generateResponse("Search results", HttpStatus.OK, services, "/service/search");
        }


        @GetMapping("/count/byservicename/{serviceName}")  //response, total count + names of garages
        public ResponseEntity<Object> getUniqueGarageCountByServiceName(@PathVariable String serviceName) {
            long count = servicesService.countGaragesByServiceName(serviceName);
            return ResponseHandler.generateResponse("Service count by service name", HttpStatus.OK, count, "/service/count/byservicename/{serviceName}");
        }

        @GetMapping("/search/{garageId}")
        public ResponseEntity<Object> getAllServicesByGarageId(@PathVariable("garageId") Long garageId){
            List<ServiceResponseDTO> services = servicesService.getServicesByGarageId(garageId);
            return ResponseHandler.generateResponse("Services offered by a garage", HttpStatus.OK, services, "/service/search/{garageId}");
        }

        @GetMapping("/all")
        public ResponseEntity<Object> getAllServices() {
            List<ServiceResponseDTO> services = servicesService.getAllServices();
            return ResponseHandler.generateResponse("Garage Services", HttpStatus.OK, services,"/service/all" );
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
        @PostMapping("/create/{categoryId}")
        public ResponseEntity<Object> createService(
                @PathVariable Long categoryId,
                @RequestBody ServiceRequestDTO serviceRequestDTO){
            ServiceResponseDTO service = servicesService.createService(categoryId, serviceRequestDTO);
            return ResponseHandler.generateResponse("Service created successfully", HttpStatus.OK, service,"/service/create/{categoryId}" );
        }

        @GetMapping("/any-search/{keyword}")
        public List<ServiceResponseDTO> searchByKeyword(@PathVariable String keyword) {
            return servicesService.searchServicess(keyword);
        }


    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN')")
        @PutMapping("/update/{serviceId}")
        public ResponseEntity<Object> updateService(@PathVariable Long serviceId, @RequestBody ServiceRequestDTO serviceRequestDTO){
            ServiceRequestDTO service = servicesService.updateService(serviceId, serviceRequestDTO);
            return ResponseHandler.generateResponse("Service updated successfully", HttpStatus.OK, service,"/service/update/{serviceId}" );
        }

        @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
        @DeleteMapping("/delete/{serviceId}")
        public ResponseEntity<Object> deleteAService(@PathVariable("serviceId") Long serviceId){
            ServiceResponseDTO service = servicesService.deleteService(serviceId);
            return ResponseHandler.generateResponse("Service Deleted Successfully", HttpStatus.OK, service, "/service/delete/{serviceId}");
        }
}
