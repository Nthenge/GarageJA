package com.eclectics.Garage.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "severity")
public class SeverityCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String severityName;

//    @OneToMany(mappedBy = "severityCategories", cascade = CascadeType.ALL, orphanRemoval = true )
//    @JsonManagedReference
//    private List<ServiceRequest> serviceRequests;

//    @OneToMany(mappedBy = "severityCategories", cascade = CascadeType.ALL, orphanRemoval = true )
//    @JsonManagedReference
//    private List<CarOwner> carOwners = new ArrayList<>();

//    @OneToMany(mappedBy = "severityCategories", cascade = CascadeType.ALL, orphanRemoval = true )
//    private List<Service> services = new ArrayList<>();

    public SeverityCategories() {}

    public SeverityCategories(Long id, String severityName, List<CarOwner> carOwners, List<Service> services) {
        this.id = id;
        this.severityName = severityName;
//        this.carOwners = carOwners;
//        this.services = services;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getSeverityName() { return severityName;}
    public void setSeverityName(String severityName) { this.severityName = severityName;}

//    public List<CarOwner> getCarOwners() {return carOwners;}
//    public void setCarOwners(List<CarOwner> carOwners) {this.carOwners = carOwners;}

//    public List<Service> getServices() {return services;}
//    public void setServices(List<Service> services) {this.services = services;}
}