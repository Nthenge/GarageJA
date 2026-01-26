package com.eclectics.Garage.dto;

import java.util.List;

public class GarageResponseDTO {
    private String licenseNumber;
    private String professionalCertificate;
    private String facilityPhotos;

    private Long garageId;
    private List<String> operatingDays;

    private String registrationNumber;
    private String businessEmail;
    private String closingTime;
    private String openingTime;
    private List<Long> services;

    private String businessName;
    private String physicalAddress;
    private String phoneNumber;

    private Double latitude;
    private Double longitude;

    private Integer yearsInOperation;
    private Integer paybillNumber;
    private Integer accountNumber;
    private Integer mpesaTill;

    public GarageResponseDTO() {}

    public GarageResponseDTO(String businessLicense,Integer accountNumber, Double latitude, Double longitude, String professionalCertificate, String closingTime, String openingTime,String facilityPhotos, Long garageId, List<String> operatingDays, String businessRegNumber, String businessEmail, String phoneNumber, Integer paybillNumber, Integer yearsInOperation, String businessName, List<Long>  services, String physicalAddress, Integer mpesaTill) {
        this.licenseNumber = businessLicense;
        this.accountNumber = accountNumber;
        this.professionalCertificate = professionalCertificate;
        this.facilityPhotos = facilityPhotos;
        this.garageId = garageId;
        this.operatingDays = operatingDays;
        this.registrationNumber = businessRegNumber;
        this.businessEmail = businessEmail;
        this.phoneNumber = phoneNumber;
        this.paybillNumber = paybillNumber;
        this.yearsInOperation = yearsInOperation;
        this.businessName = businessName;
        this.closingTime = closingTime;
        this.openingTime = openingTime;
        this.services = services;
        this.physicalAddress = physicalAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mpesaTill = mpesaTill;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getProfessionalCertificate() {
        return professionalCertificate;
    }

    public void setProfessionalCertificate(String professionalCertificate) {
        this.professionalCertificate = professionalCertificate;
    }

    public String getFacilityPhotos() {
        return facilityPhotos;
    }

    public void setFacilityPhotos(String facilityPhotos) {
        this.facilityPhotos = facilityPhotos;
    }

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public List<String> getOperatingDays() {
        return operatingDays;
    }

    public void setOperatingDays(List<String> operatingDays) {
        this.operatingDays = operatingDays;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBusinessEmailAddress() {
        return businessEmail;
    }

    public void setBusinessEmailAddress(String businessEmailAddress) {
        this.businessEmail = businessEmailAddress;
    }

    public List<Long>  getServices() {
        return services;
    }

    public void setServices(List<Long>  services) {
        this.services = services;
    }
    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getYearsInOperation() {
        return yearsInOperation;
    }

    public void setYearsInOperation(Integer yearsInOperation) {
        this.yearsInOperation = yearsInOperation;
    }

    public Integer getPaybillNumber() {
        return paybillNumber;
    }

    public void setPaybillNumber(Integer paybillNumber) {
        this.paybillNumber = paybillNumber;
    }

    public Integer getMpesaTill() {
        return mpesaTill;
    }

    public void setMpesaTill(Integer mpesaTill) {
        this.mpesaTill = mpesaTill;
    }
}
