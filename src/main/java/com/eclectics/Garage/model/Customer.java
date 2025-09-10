package com.eclectics.Garage.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer uniqueId;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequest> requests = new ArrayList<>();

    public Customer() {}

    public Customer(Long id, Integer uniqueId, String fullName, String email, String phoneNumber, String password) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUniqueId() { return uniqueId;}
    public void setUniqueId(Integer uniqueId) { this.uniqueId = uniqueId;}

    public String getFullName() { return fullName;}
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber; }

    public String getPassword(){return password; }
    public void setPassword(String password){ this.password = password; }

}


