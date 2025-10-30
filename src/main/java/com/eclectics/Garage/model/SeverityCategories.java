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

    public SeverityCategories() {}

    public SeverityCategories(Long id, String severityName, List<CarOwner> carOwners, List<Service> services) {
        this.id = id;
        this.severityName = severityName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getSeverityName() { return severityName;}
    public void setSeverityName(String severityName) { this.severityName = severityName;}
}