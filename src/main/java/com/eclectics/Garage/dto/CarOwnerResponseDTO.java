package com.eclectics.Garage.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;

public class CarOwnerResponseDTO {
    private Long id;
    private Integer uniqueId;
    private String altPhone;
    private String make;
    private String model;
    private String year;
    private String licensePlate;
    private String engineType;
    private String engineCapacity;
    private String color;
    private String transmission;
    private String severity;
    private boolean isComplete;
    private String profilePic;

    public CarOwnerResponseDTO() {
    }

    public CarOwnerResponseDTO(Long id, String altPhone, String year, String engineCapacity, String severity, String profilePic, boolean isComplete, String transmission, String engineType, String make, Integer uniqueId, String model, String licensePlate, String color) {
        this.id = id;
        this.altPhone = altPhone;
        this.year = year;
        this.engineCapacity = engineCapacity;
        this.severity = severity;
        this.profilePic = profilePic;
        this.isComplete = isComplete;
        this.transmission = transmission;
        this.engineType = engineType;
        this.make = make;
        this.uniqueId = uniqueId;
        this.model = model;
        this.licensePlate = licensePlate;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAltPhone() {
        return altPhone;
    }

    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
