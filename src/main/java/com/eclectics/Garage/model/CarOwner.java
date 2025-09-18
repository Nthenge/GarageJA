package com.eclectics.Garage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carOwners")
public class CarOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
        private Integer uniqueId;

    @Lob
    @Column(unique = false, nullable = true)
    private byte[] profilePic;

    @Column(unique = false, nullable = true)
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

    @OneToMany(mappedBy = "carOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequest> requests = new ArrayList<>();

    // make this nullable false
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true, unique = true)
    @JsonBackReference
    private User user;

    public CarOwner() {}

    public CarOwner(Long id, Integer uniqueId,User user, byte[] profilePic,String model,String severity, String altPhone,String year,String transmission, String make,String engineType, String licensePlate, String engineCapacity, String color) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.make = make;
        this.profilePic = profilePic;
        this.altPhone = altPhone;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.engineType = engineType;
        this.engineCapacity = engineCapacity;
        this.color = color;
        this.transmission = transmission;
        this.severity = severity;
        this.user = user;
    }

    @Transient
    public boolean isComplete() {
        return uniqueId != null
                && profilePic != null && profilePic.length > 0
                && altPhone != null && !altPhone.isBlank()
                && model != null && !model.isBlank()
                && licensePlate != null && !licensePlate.isBlank()
                && engineCapacity != null && !engineCapacity.isBlank()
                && color != null && !color.isBlank()
                && make != null && !make.isBlank()
                && year != null && !year.isBlank()
                && engineType != null && !engineType.isBlank()
                && transmission != null && !transmission.isBlank()
                && severity != null && !severity.isBlank()
                && user != null;
    }


    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public Integer getUniqueId() { return uniqueId;}
    public void setUniqueId(Integer uniqueId) {this.uniqueId = uniqueId;}

    public String getMake() {return make;}
    public void setMake(String make) {this.make = make;}

    public byte[] getProfilePic() {return profilePic;}
    public void setProfilePic(byte[] profilePic) {this.profilePic = profilePic;}

    public String getAltPhone() {return altPhone;}
    public void setAltPhone(String altPhone) {this.altPhone = altPhone;}

    public String getModel() {return model;}
    public void setModel(String model) {this.model = model;}

    public String getYear() {return year;}
    public void setYear(String year) {this.year = year;}

    public String getLicensePlate() {return licensePlate;}
    public void setLicensePlate(String licensePlate) {this.licensePlate = licensePlate;}

    public String getEngineType() {return engineType;}
    public void setEngineType(String engineType) {this.engineType = engineType;}

    public String getEngineCapacity() {return engineCapacity;}
    public void setEngineCapacity(String engineCapacity) {this.engineCapacity = engineCapacity;}

    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}

    public String getTransmission() {return transmission;}
    public void setTransmission(String transmission) {this.transmission = transmission;}

    public String getSeverity() {return severity;}
    public void setSeverity(String severity) {this.severity = severity;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}


