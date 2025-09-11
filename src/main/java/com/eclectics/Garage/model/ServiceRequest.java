package com.eclectics.Garage.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carOwner_id", referencedColumnName = "uniqueId")
    private CarOwner carOwner;

    @ManyToOne
    @JoinColumn(name = "garage_id", referencedColumnName = "garageId")
    private Garage garage;

    @ManyToOne
    @JoinColumn(name = "service_definition_id", referencedColumnName = "id")
    private Service service;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private AssignMechanics assignRequests;

    public ServiceRequest() {}

    public ServiceRequest(long id, CarOwner carOwner, Garage garage, Service service, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.carOwner = carOwner;
        this.garage = garage;
        this.service = service;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id;}

    public CarOwner getCarOwner() { return carOwner;}
    public void setCarOwner(CarOwner carOwner) { this.carOwner = carOwner; }

    public Garage getGarage() { return garage; }
    public void setGarage(Garage garage) { this.garage = garage;}

    public Status getStatus() {return status;}
    public void setStatus(Status status) { this.status = status;}

    public LocalDateTime getCreatedAt() { return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() { return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}
