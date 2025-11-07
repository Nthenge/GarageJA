package com.eclectics.Garage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "services")

public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String description;
    private Double price;
    private Double avgDuration;

    @ManyToOne
    @JoinColumn(name = "garageId", referencedColumnName = "garageId")
    private Garage garage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference()
    private ServiceCategories serviceCategories;

    public Service() {}

    public Service(Long id, String serviceName, String description, Double price, Double avgDuration, Garage garage, ServiceCategories serviceCategories) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.avgDuration = avgDuration;
        this.garage = garage;
        this.serviceCategories = serviceCategories;
    }

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id; }

    public String getServiceName() { return serviceName;}
    public void setServiceName(String serviceName) {this.serviceName = serviceName;}

    public String getDescription() { return description;}
    public void setDescription(String description) { this.description = description;}

    public Double getPrice() { return price;}
    public void setPrice(Double price) { this.price = price; }

    public Garage getGarage() { return garage; }
    public void setGarage(Garage garage) { this.garage = garage; }

    public Double getAvgDuration() { return avgDuration;}
    public void setAvgDuration(Double avgDuration) {this.avgDuration = avgDuration; }

    public ServiceCategories getServiceCategories() {return serviceCategories;}
    public void setServiceCategories(ServiceCategories serviceCategories) {this.serviceCategories = serviceCategories;}
}

