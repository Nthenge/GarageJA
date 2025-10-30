package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.ServiceCategories;

public class ServiceResponseDTO {

    private String serviceName;
    private String description;
    private Double price;
    private Garage garage;
    private ServiceCategories serviceCategories;

    public ServiceResponseDTO() {}

    public ServiceResponseDTO(String serviceName, String description, Double price, Garage garage, ServiceCategories serviceCategories) {
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.garage = garage;
        this.serviceCategories = serviceCategories;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public ServiceCategories getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(ServiceCategories serviceCategories) {
        this.serviceCategories = serviceCategories;
    }
}


