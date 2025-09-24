package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Garage;

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

    private byte[] profilePic;
    private byte[] nationalIDPic;
    private byte[] professionalCertfificate;
    private byte[] anyRelevantCertificate;
    private byte[] policeClearanceCertficate;
    private Garage garage;

    public MechanicRequestDTO() {}

    public MechanicRequestDTO(String areasofSpecialization,Garage garage, String alternativePhone, String physicalAddress, String emergencyContactName, String emergencyContactNumber, String yearsofExperience, String vehicleBrands, String availability, Integer nationalIdNumber, byte[] profilePic, byte[] nationalIDPic, byte[] professionalCertfificate, byte[] anyRelevantCertificate, byte[] policeClearanceCertficate) {
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
        this.garage = garage;
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

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public byte[] getNationalIDPic() {
        return nationalIDPic;
    }

    public void setNationalIDPic(byte[] nationalIDPic) {
        this.nationalIDPic = nationalIDPic;
    }

    public byte[] getProfessionalCertfificate() {
        return professionalCertfificate;
    }

    public void setProfessionalCertfificate(byte[] professionalCertfificate) {
        this.professionalCertfificate = professionalCertfificate;
    }

    public byte[] getAnyRelevantCertificate() {
        return anyRelevantCertificate;
    }

    public void setAnyRelevantCertificate(byte[] anyRelevantCertificate) {
        this.anyRelevantCertificate = anyRelevantCertificate;
    }

    public byte[] getPoliceClearanceCertficate() {
        return policeClearanceCertficate;
    }

    public void setPoliceClearanceCertficate(byte[] policeClearanceCertficate) {
        this.policeClearanceCertficate = policeClearanceCertficate;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
}
