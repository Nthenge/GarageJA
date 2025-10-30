package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Role;

import java.util.List;

public class UserDetailsAuthDTO extends UserLoginResponseDTO{
    private String firstname;
    private boolean detailsCompleted;
    private List<String> missingFields;

    public UserDetailsAuthDTO() {
    }

    public UserDetailsAuthDTO(String token, Role role, String firstname, boolean detailsCompleted, List<String> missingFields) {
        super(token, role);
        this.firstname = firstname;
        this.detailsCompleted = detailsCompleted;
        this.missingFields = missingFields;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public boolean isDetailsCompleted() {
        return detailsCompleted;
    }

    public void setDetailsCompleted(boolean detailsCompleted) {
        this.detailsCompleted = detailsCompleted;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public void setMissingFields(List<String> missingFields) {
        this.missingFields = missingFields;
    }
}
