package com.eclectics.Garage.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "service_categories")
public class ServiceCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceCategoryName;

    @OneToMany(mappedBy = "serviceCategories", cascade = CascadeType.ALL, orphanRemoval = true )
    @JsonManagedReference
    private List<Service> services;

    public ServiceCategories(){}

    public ServiceCategories(String serviceCategoryName, Long id){
        this.serviceCategoryName = serviceCategoryName;
        this.id = id;
    }

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getServiceCategoryName() { return serviceCategoryName;}
    public void setServiceCategoryName(String serviceCategoryName) {this.serviceCategoryName = serviceCategoryName;}

    public List<Service> getServices() { return services;}
    public void setServices(List<Service> services) {this.services = services;}
}
