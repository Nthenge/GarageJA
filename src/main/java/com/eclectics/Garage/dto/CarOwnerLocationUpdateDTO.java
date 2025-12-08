package com.eclectics.Garage.dto;

public class CarOwnerLocationUpdateDTO {
    private Double latitude;
    private Double longitude;

    public CarOwnerLocationUpdateDTO() {
    }

    public CarOwnerLocationUpdateDTO(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
