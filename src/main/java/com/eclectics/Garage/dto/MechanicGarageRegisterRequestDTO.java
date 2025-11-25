package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Role;
import jakarta.validation.constraints.NotBlank;

public class MechanicGarageRegisterRequestDTO {
        @NotBlank
        private String firstname;

        @NotBlank
        private String secondname;

        @NotBlank
        private String email;

        @NotBlank
        private String phoneNumber;

        private String password;

        private Role role;

    public MechanicGarageRegisterRequestDTO(String firstname, String phoneNumber, String password, Role role, String email, String secondname) {
        this.firstname = firstname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.email = email;
        this.secondname = secondname;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
