package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;
import org.springframework.web.multipart.MultipartFile;

public class MechanicRequestDTO {
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

    public MechanicRequestDTO() {}

    public MechanicRequestDTO(String areasofSpecialization, String alternativePhone, String physicalAddress, String emergencyContactName, String emergencyContactNumber, String yearsofExperience, String vehicleBrands, String availability, Integer nationalIdNumber, String profilePic, String nationalIDPic, String professionalCertfificate, String anyRelevantCertificate, String policeClearanceCertficate) {
        this.areasofSpecialization = areasofSpecialization;
        this.alternativePhone = alternativePhone;
        this.physicalAddress = physicalAddress;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactNumber = emergencyContactNumber;
        this.yearsofExperience = yearsofExperience;
        this.vehicleBrands = vehicleBrands;
        this.availability = availability;
        this.nationalIdNumber = nationalIdNumber;
        this.profilePic = profilePic;
        this.nationalIDPic = nationalIDPic;
        this.professionalCertfificate = professionalCertfificate;
        this.anyRelevantCertificate = anyRelevantCertificate;
        this.policeClearanceCertficate = policeClearanceCertficate;
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

    public String getVehicleBrands() {
        return vehicleBrands;
    }

    public void setVehicleBrands(String vehicleBrands) {
        this.vehicleBrands = vehicleBrands;
    }

    public Integer getNationalIdNumber() {
        return nationalIdNumber;
    }

    public void setNationalIdNumber(Integer nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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
}
