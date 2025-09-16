package com.eclectics.Garage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "automobiles")
public class AutoMobiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String make;
    private String year;
    private String engineType;
    private String transmission;

    public AutoMobiles() {}

    public AutoMobiles(Long id, String make, String year, String engineType, String transmission) {
        this.make = make;
        this.year = year;
        this.engineType = engineType;
        this.transmission = transmission;
        this.id = id;
    }

    public Long getId() {return id;}
    public void setId(Long id){this.id = id;}

    public String getMake() { return make;}
    public void setMake(String make) {this.make = make;}

    public String getYear() {return year;}
    public void setYear(String year) {this.year = year;}

    public String getEngineType() {return engineType;}
    public void setEngineType(String engineType) {this.engineType = engineType;}

    public String getTransmission() {return transmission;}
    public void setTransmission(String transmission) {this.transmission = transmission;}
}
