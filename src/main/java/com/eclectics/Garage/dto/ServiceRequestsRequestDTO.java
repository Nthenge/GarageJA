package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class ServiceRequestsRequestDTO {

    private CarOwner carOwner;
    private Garage garage;
    private Service service;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SeverityCategories severityCategories;
    private RequestStatus status;

    public ServiceRequestsRequestDTO() {}

    public ServiceRequestsRequestDTO(CarOwner carOwner, Garage garage, Service service, LocalDateTime createdAt, LocalDateTime updatedAt, SeverityCategories severityCategories, RequestStatus status) {
        this.carOwner = carOwner;
        this.garage = garage;
        this.service = service;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.severityCategories = severityCategories;
        this.status = status;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public SeverityCategories getSeverityCategories() {
        return severityCategories;
    }

    public void setSeverityCategories(SeverityCategories severityCategories) {
        this.severityCategories = severityCategories;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
