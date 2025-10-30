package com.eclectics.Garage.dto;

public class SeverityResponseDTO {

    private String severityName;

    public SeverityResponseDTO() {
    }

    public SeverityResponseDTO(String severityName) {
        this.severityName = severityName;
    }

    public String getSeverityName() {
        return severityName;
    }

    public void setSeverityName(String severityName) {
        this.severityName = severityName;
    }
}
