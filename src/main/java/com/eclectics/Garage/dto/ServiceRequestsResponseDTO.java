package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.*;

import java.time.LocalDateTime;

public class ServiceRequestsResponseDTO {

    private Long carId;
    private Long garageId;
    private String garageName;
    private Long serviceId;
    private String serviceName;
    private Long severityId;
    private String severityName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RequestStatus status;

    public ServiceRequestsResponseDTO() {}

    public ServiceRequestsResponseDTO(Long carId, Long garageId, String garageName, Long serviceId, String serviceName, Long severityId, LocalDateTime createdAt, RequestStatus status, LocalDateTime updatedAt, String severityName) {
        this.carId = carId;
        this.garageId = garageId;
        this.garageName = garageName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.severityId = severityId;
        this.createdAt = createdAt;
        this.status = status;
        this.updatedAt = updatedAt;
        this.severityName = severityName;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getSeverityId() {
        return severityId;
    }

    public void setSeverityId(Long severityId) {
        this.severityId = severityId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSeverityName() {
        return severityName;
    }

    public void setSeverityName(String severityName) {
        this.severityName = severityName;
    }
}
