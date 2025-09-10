package com.eclectics.Garage.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_assignments")
public class AssignMechanics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ServiceRequest service;

    @ManyToOne
    @JoinColumn(name = "mechanic_id", referencedColumnName = "nationalIdNumber")
    private Mechanic mechanic;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime updatedAt;

    public AssignMechanics() {}

    public AssignMechanics(Long id, ServiceRequest service, Mechanic mechanic, AssignmentStatus status, LocalDateTime assignedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.service = service;
        this.mechanic = mechanic;
        this.status = status;
        this.assignedAt = assignedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceRequest getService() { return service;}
    public void setService(ServiceRequest service) { this.service = service;}

    public Mechanic getMechanic() { return mechanic;}
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic;}

    public AssignmentStatus getStatus() { return status;}
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt;}

    public LocalDateTime getUpdatedAt() { return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
