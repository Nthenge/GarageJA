package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.*;

import java.time.LocalDateTime;

public class ServiceRequestsResponseDTO {

    private CarOwner carOwner;
    private Garage garage;
    private Service service;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SeverityCategories severity;
    private RequestStatus status;

    public ServiceRequestsResponseDTO() {}

    public ServiceRequestsResponseDTO(CarOwner carOwner, Garage garage, Service service, LocalDateTime createdAt, LocalDateTime updatedAt, SeverityCategories severity, RequestStatus status) {
        this.carOwner = carOwner;
        this.garage = garage;
        this.service = service;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.severity = severity;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SeverityCategories getSeverityCategories() {
        return severity;
    }

    public void setSeverityCategories(SeverityCategories severity) {
        this.severity = severity;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
