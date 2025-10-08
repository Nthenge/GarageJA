package com.eclectics.Garage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mechanics")
public class Mechanic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String areasofSpecialization;
    private String alternativePhone;
    private String physicalAddress;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String yearsofExperience;
    private String vehicleBrands;
    private String availability;

    @Lob
    private byte[] profilePic;
    @Lob
    private byte[] nationalIDPic;
    @Lob
    private byte[] professionalCertfificate;
    @Lob
    private byte[] anyRelevantCertificate;
    @Lob
    private byte[] policeClearanceCertficate;

    @Column(unique = true)
    private Integer nationalIdNumber;

    @ManyToOne
    @JoinColumn(name = "garageId", referencedColumnName = "garageId")
    private Garage garage;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    public Mechanic() {}

    public Mechanic(Long id, String areasofSpecialization, User user, String alternativePhone, String physicalAddress, String emergencyContactName, String emergencyContactNumber, String yearsofExperience, String vehicleBrands, String availability, byte[] profilePic, byte[] nationalIDPic, byte[] professionalCertfificate, byte[] anyRelevantCertificate, byte[] policeClearanceCertficate, Integer nationalIdNumber, Garage garage) {
        this.id = id;
        this.areasofSpecialization = areasofSpecialization;
        this.alternativePhone = alternativePhone;
        this.physicalAddress = physicalAddress;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactNumber = emergencyContactNumber;
        this.yearsofExperience = yearsofExperience;
        this.vehicleBrands = vehicleBrands;
        this.availability = availability;
        this.profilePic = profilePic;
        this.nationalIDPic = nationalIDPic;
        this.professionalCertfificate = professionalCertfificate;
        this.anyRelevantCertificate = anyRelevantCertificate;
        this.policeClearanceCertficate = policeClearanceCertficate;
        this.nationalIdNumber = nationalIdNumber;
        this.garage = garage;
        this.user = user;
    }

    @Transient
    public boolean isComplete() {
        return getMissingFields().isEmpty();
    }

    @Transient
    public List<String> getMissingFields() {
        List<String> missingFields = new ArrayList<>();

        if (areasofSpecialization != null && !areasofSpecialization.isBlank()) {missingFields.add("areasofSpecialization");};
        if (physicalAddress == null || physicalAddress.isBlank()) missingFields.add("physicalAddress");
        if (yearsofExperience == null || yearsofExperience.isBlank()) missingFields.add("yearsofExperience");
        if (vehicleBrands == null || vehicleBrands.isBlank()) missingFields.add("vehicleBrands");
        if (availability == null || availability.isBlank()) missingFields.add("availability");
        if (professionalCertfificate == null || professionalCertfificate.length == 0) missingFields.add("professionalCertfificate");
        if (policeClearanceCertficate == null || policeClearanceCertficate.length == 0) missingFields.add("policeClearanceCertficate");
        if (nationalIdNumber == null) missingFields.add("nationalIdNumber");
//        if (garage == null) missingFields.add("garage"); return it to be a must

        return missingFields;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getAreasofSpecialization() { return areasofSpecialization;}
    public void setAreasofSpecialization(String areasofSpecialization) { this.areasofSpecialization = areasofSpecialization;}

    public String getAlternativePhone() { return alternativePhone;}
    public void setAlternativePhone(String alternativePhone) { this.alternativePhone = alternativePhone;}

    public String getPhysicalAddress() { return physicalAddress;}
    public void setPhysicalAddress(String physicalAddress) { this.physicalAddress = physicalAddress;}

    public String getEmergencyContactName() { return emergencyContactName;}
    public void setEmergencyContactName(String emergencyContactName) {this.emergencyContactName = emergencyContactName;}

    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) {this.emergencyContactNumber = emergencyContactNumber;}

    public String getYearsofExperience() { return yearsofExperience;}
    public void setYearsofExperience(String yearsofExperience) {this.yearsofExperience = yearsofExperience;}

    public String getVehicleBrands() { return vehicleBrands; }
    public void setVehicleBrands(String vehicleBrands) { this.vehicleBrands = vehicleBrands;}

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability;}

    public byte[] getProfilePic() { return profilePic;}
    public void setProfilePic(byte[] profilePic) { this.profilePic = profilePic;}

    public byte[] getNationalIDPic() { return nationalIDPic;}
    public void setNationalIDPic(byte[] nationalID) { this.nationalIDPic = nationalID;}

    public byte[] getProfessionalCertfificate() { return professionalCertfificate;}
    public void setProfessionalCertfificate(byte[] professionalCertfificate) {this.professionalCertfificate = professionalCertfificate;}

    public byte[] getAnyRelevantCertificate() { return anyRelevantCertificate;}
    public void setAnyRelevantCertificate(byte[] anyRelevantCertificate) {this.anyRelevantCertificate = anyRelevantCertificate;}

    public byte[] getPoliceClearanceCertficate() { return policeClearanceCertficate;}
    public void setPoliceClearanceCertficate(byte[] policeClearanceCertficate) { this.policeClearanceCertficate = policeClearanceCertficate;}

    public Integer getNationalIdNumber() { return nationalIdNumber;}
    public void setNationalIdNumber(Integer nationalIdNumber) { this.nationalIdNumber = nationalIdNumber;}

    public Garage getGarage() { return garage;}
    public void setGarage(Garage garage) { this.garage = garage;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}

