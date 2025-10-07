package com.eclectics.Garage.dto;

public class AutoMobileResponseDTO {
    private String make;
    private String year;
    private String engineType;
    private String transmission;

    public AutoMobileResponseDTO(String make, String year, String engineType, String transmission) {
        this.make = make;
        this.year = year;
        this.engineType = engineType;
        this.transmission = transmission;
    }

    public AutoMobileResponseDTO() {}

    public String getMake() { return make;}
    public void setMake(String make) { this.make = make;}

    public String getYear() { return year;}
    public void setYear(String year) { this.year = year;}

    public String getEngineType() { return engineType;}
    public void setEngineType(String engineType) { this.engineType = engineType;}

    public String getTransmission() {return transmission;}
    public void setTransmission(String transmission) { this.transmission = transmission;}
}
