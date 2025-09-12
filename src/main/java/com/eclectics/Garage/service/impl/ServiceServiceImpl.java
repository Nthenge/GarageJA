package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Service;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.ServiceRepository;
import com.eclectics.Garage.service.ServicesService;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServicesService {

    private final ServiceRepository serviceRepository;
    private final GarageRepository garageRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository, GarageRepository garageRepository) {
        this.serviceRepository = serviceRepository;
        this.garageRepository = garageRepository;
    }

    @Override
    public Service createService(Service service) {
        if (service.getGarage() != null && service.getGarage().getGarageId() != null) {
            Long garageAdminId = service.getGarage().getGarageId();

            Garage garage = garageRepository.findByGarageId(garageAdminId)
                    .orElseThrow(() -> new RuntimeException("Garage with this id " + garageAdminId + " not found"));

            service.setGarage(garage);
        }
        return serviceRepository.save(service);
    }

    public List<Service> getAllServicesByGarageId(Long garageId){
        return serviceRepository.findByGarage_GarageId(garageId);
    }

    @Override
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public List<Service> getServicesByGarageId(Long garageId) {
        return serviceRepository.findByGarage_GarageId(garageId);
    }

    @Override
    public Service updateService(Long id, Service service) {
        return serviceRepository.findById(id).map(existingService -> {
            if (service.getServiceName() != null) existingService.setServiceName(service.getServiceName());
            if (service.getServiceName() != null) existingService.setDescription(service.getDescription());
            if (service.getServiceName() != null) existingService.setPrice(service.getPrice());
            if (service.getServiceName() != null) existingService.setGarage(service.getGarage());
            if (service.getServiceCategories() != null) existingService.setServiceCategories(service.getServiceCategories());
            //if (service.getSeverityCategories() != null) existingService.setSeverityCategories(service.getSeverityCategories());
            return serviceRepository.save(existingService);
        }).orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    public String deleteService(Long id) {
        serviceRepository.deleteById(id);
        return "Service Deleted";
    }
}
