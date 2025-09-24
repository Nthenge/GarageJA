package com.eclectics.Garage.dto;

import java.util.List;

public class ProfileCompleteDTO {
    private boolean completed;
    private List<String> missingFields;

    public ProfileCompleteDTO(boolean completed, List<String> missingFields) {
        this.completed = completed;
        this.missingFields = missingFields;
    }

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}

    public List<String> getMissingFields() {return missingFields;}
    public void setMissingFields(List<String> missingFields) {this.missingFields = missingFields;}
}
