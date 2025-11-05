package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.ServiceCategories;

public class ServiceResponseDTO {

    private Long id;
    private String serviceName;
    private String description;
    private Double price;
    private Double avgDuration;
    private Long garageId;
    private String garageName;
    private Long categoryId;
    private String categoryName;

    public ServiceResponseDTO() {}

    public ServiceResponseDTO(Long id, String serviceName, String description,Double avgDuration, Long garageId, String garageName, Long categoryId, String categoryName, Double price) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.garageId = garageId;
        this.garageName = garageName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.avgDuration = avgDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}


