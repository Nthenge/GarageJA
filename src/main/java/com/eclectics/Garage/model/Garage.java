package com.eclectics.Garage.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garages")
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long garageId;

    @Column(name = "garage_name")
    private String name;

    @Column(name = "garage_location")
    private String location;

    @Column(name = "garage_phone_number")
    private String phone;

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services = new ArrayList<>();


    public Garage(Long id, String name, String location, String phone, Long garageId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.garageId = garageId;
    }

    public Garage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getName() { return name;}
    public void setName(String name) { this.name = name;}

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location;}

    public String getPhone() { return phone;}
    public void setPhone(String phone) { this.phone = phone;}

    public Long getGarageId() { return garageId;}
    public void setGarageId(Long garageId) { this.garageId = garageId;}
}
