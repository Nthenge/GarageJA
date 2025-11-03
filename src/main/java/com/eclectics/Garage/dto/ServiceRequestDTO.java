package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.ServiceCategories;

public class ServiceRequestDTO {

    private String serviceName;
    private String description;
    private Double price;
    private Long garageId;
    private Long categoryId;

    public ServiceRequestDTO() {}

    public ServiceRequestDTO(String serviceName, String description, Double price, Long garageId, Long categoryId) {
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.garageId = garageId;
        this.categoryId = categoryId;
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

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
