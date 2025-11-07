package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.CarOwner;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.Role;

public class UserRegistrationResponseDTO {
    private Long id;
    private String email;
    private String firstname;
    private String secondname;
    private String phoneNumber;
    private Role role;
    private Mechanic mechanic;
    private CarOwner carOwner;
    private Garage garage;
    private boolean enabled;
    private boolean isDetailsCompleted;

    public UserRegistrationResponseDTO() {}

    public UserRegistrationResponseDTO(Long id, String email, boolean isDetailsCompleted, String phoneNumber, String firstname, String secondname, Role role, Mechanic mechanic, CarOwner carOwner, Garage garage, boolean enabled) {
        this.id = id;
        this.email = email;
        this.isDetailsCompleted = isDetailsCompleted;
        this.phoneNumber = phoneNumber;
        this.firstname = firstname;
        this.secondname = secondname;
        this.role = role;
        this.mechanic = mechanic;
        this.carOwner = carOwner;
        this.garage = garage;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDetailsCompleted() {
        return isDetailsCompleted;
    }

    public void setDetailsCompleted(boolean detailsCompleted) {
        isDetailsCompleted = detailsCompleted;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
