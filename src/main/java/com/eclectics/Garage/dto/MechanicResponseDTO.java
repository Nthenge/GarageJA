package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

public class MechanicResponseDTO {
    private Long id;
    private String areasofSpecialization;
    private String alternativePhone;
    private String physicalAddress;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String yearsofExperience;
    private String vehicleBrands;
    private String availability;
    private Integer nationalIdNumber;
    private String profilePic;
    private String nationalIDPic;
    private String professionalCertfificate;
    private String anyRelevantCertificate;
    private String policeClearanceCertficate;
    private Garage garage;

    public MechanicResponseDTO() {}

    public MechanicResponseDTO(Long id, String areasofSpecialization, String emergencyContactNumber,Garage garage, Integer nationalIdNumber, String profilePic,String nationalIDPic,String professionalCertfificate, String anyRelevantCertificate, String policeClearanceCertficate, String availability, String vehicleBrands, String yearsofExperience, String emergencyContactName, String physicalAddress, String alternativePhone) {
        this.id = id;
        this.areasofSpecialization = areasofSpecialization;
        this.emergencyContactNumber = emergencyContactNumber;
        this.nationalIdNumber = nationalIdNumber;
        this.profilePic = profilePic;
        this.availability = availability;
        this.vehicleBrands = vehicleBrands;
        this.yearsofExperience = yearsofExperience;
        this.emergencyContactName = emergencyContactName;
        this.physicalAddress = physicalAddress;
        this.alternativePhone = alternativePhone;
        this.nationalIDPic = nationalIDPic;
        this.professionalCertfificate = professionalCertfificate;
        this.anyRelevantCertificate = anyRelevantCertificate;
        this.policeClearanceCertficate = policeClearanceCertficate;
        this.garage = garage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreasofSpecialization() {
        return areasofSpecialization;
    }

    public void setAreasofSpecialization(String areasofSpecialization) {
        this.areasofSpecialization = areasofSpecialization;
    }

    public String getAlternativePhone() {
        return alternativePhone;
    }

    public void setAlternativePhone(String alternativePhone) {
        this.alternativePhone = alternativePhone;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getYearsofExperience() {
        return yearsofExperience;
    }

    public void setYearsofExperience(String yearsofExperience) {
        this.yearsofExperience = yearsofExperience;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getVehicleBrands() {
        return vehicleBrands;
    }

    public void setVehicleBrands(String vehicleBrands) {
        this.vehicleBrands = vehicleBrands;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Integer getNationalIdNumber() {
        return nationalIdNumber;
    }

    public void setNationalIdNumber(Integer nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    public String getNationalIDPic() {
        return nationalIDPic;
    }

    public void setNationalIDPic(String nationalIDPic) {
        this.nationalIDPic = nationalIDPic;
    }

    public String getProfessionalCertfificate() {
        return professionalCertfificate;
    }

    public void setProfessionalCertfificate(String professionalCertfificate) {
        this.professionalCertfificate = professionalCertfificate;
    }

    public String getAnyRelevantCertificate() {
        return anyRelevantCertificate;
    }

    public void setAnyRelevantCertificate(String anyRelevantCertificate) {
        this.anyRelevantCertificate = anyRelevantCertificate;
    }

    public String getPoliceClearanceCertficate() {
        return policeClearanceCertficate;
    }

    public void setPoliceClearanceCertficate(String policeClearanceCertficate) {
        this.policeClearanceCertficate = policeClearanceCertficate;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
}
