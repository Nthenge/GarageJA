package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.ServiceRequest;

import java.time.LocalDateTime;

public class AssignMechanicsResponseDTO {

    private ServiceRequest service;
    private Mechanic mechanic;
    private AssignMechanicStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime updatedAt;

    public AssignMechanicsResponseDTO() {}

    public AssignMechanicsResponseDTO(ServiceRequest service, Mechanic mechanic, AssignMechanicStatus status, LocalDateTime assignedAt, LocalDateTime updatedAt) {
        this.service = service;
        this.mechanic = mechanic;
        this.status = status;
        this.assignedAt = assignedAt;
        this.updatedAt = updatedAt;
    }

    public ServiceRequest getService() {
        return service;
    }

    public void setService(ServiceRequest service) {
        this.service = service;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public AssignMechanicStatus getStatus() {
        return status;
    }

    public void setStatus(AssignMechanicStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
