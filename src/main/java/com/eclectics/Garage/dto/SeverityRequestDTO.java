package com.eclectics.Garage.dto;

public class SeverityRequestDTO {

    private String severityName;

    public SeverityRequestDTO() {}

    public SeverityRequestDTO(String severityName) {
        this.severityName = severityName;
    }

    public String getSeverityName() {
        return severityName;
    }

    public void setSeverityName(String severityName) {
        this.severityName = severityName;
    }
}
