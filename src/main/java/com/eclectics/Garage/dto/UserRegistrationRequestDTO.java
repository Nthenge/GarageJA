package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Role;

public class UserRegistrationRequestDTO {

    private String email;
    private String firstname;
    private String secondname;
    private String password;
    private String phoneNumber;
    private Role role;
    private boolean enabled = false;

    public UserRegistrationRequestDTO() {}

    public UserRegistrationRequestDTO(String email, String firstname, String secondname, String password, String phoneNumber, Role role, boolean enabled) {
        this.email = email;
        this.firstname = firstname;
        this.secondname = secondname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
