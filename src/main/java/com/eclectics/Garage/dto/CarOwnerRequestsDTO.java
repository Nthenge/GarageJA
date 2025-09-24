package com.eclectics.Garage.dto;

public class CarOwnerRequestsDTO {

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
    private byte[] profilePic;

    public CarOwnerRequestsDTO() {}

    public CarOwnerRequestsDTO(String altPhone, String make, String model, String year,byte[] profilePic, String licensePlate, String engineType, String engineCapacity, String color, String transmission, String severity) {
        this.altPhone = altPhone;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.engineType = engineType;
        this.engineCapacity = engineCapacity;
        this.color = color;
        this.transmission = transmission;
        this.severity = severity;
        this.profilePic = profilePic;
    }

    public String getAltPhone() {
        return altPhone;
    }
    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
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

    public byte[] getProfilePic() {return profilePic;}
    public void setProfilePic(byte[] profilePic) {this.profilePic = profilePic;}
}
