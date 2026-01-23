package com.eclectics.Garage.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class GarageRequestsDTO {
    private String licenseNumber;
    private String professionalCertificate;
    private String facilityPhotos;

    private Long garageId;
    private List<String> operatingDays;

    private String registrationNumber;
    private String businessEmail;
    private String closingTime;
    private String openingTime;
    private String serviceCategories;
    private List<Long> services;

    private String businessName;
    private String physicalAddress;
    private String phoneNumber;

    private Integer yearsInOperation;
    private Integer mpesaPayBill;
    private Integer accountNumber;
    private Integer mpesaTill;

    public GarageRequestsDTO() {}

    public GarageRequestsDTO(String businessLicense,Integer accountNumber, String professionalCertificate,String closingTime, String openingTime, String facilityPhotos, Long garageId, List<String> operatingDays, String businessRegNumber, String businessEmail, String serviceCategories, String phoneNumber, Integer mpesaPayBill, Integer yearsInOperation, String businessName, List<Long>  services, String physicalAddress, Integer mpesaTill) {
        this.licenseNumber = businessLicense;
        this.professionalCertificate = professionalCertificate;
        this.facilityPhotos = facilityPhotos;
        this.accountNumber = accountNumber;
        this.garageId = garageId;
        this.operatingDays = operatingDays;
        this.registrationNumber = businessRegNumber;
        this.businessEmail = businessEmail;
        this.serviceCategories = serviceCategories;
        this.phoneNumber = phoneNumber;
        this.mpesaPayBill = mpesaPayBill;
        this.closingTime = closingTime;
        this.openingTime = openingTime;
        this.yearsInOperation = yearsInOperation;
        this.businessName = businessName;
        this.services = services;
        this.physicalAddress = physicalAddress;
        this.mpesaTill = mpesaTill;
    }

    public String getBusinessLicense() {
        return licenseNumber;
    }

    public void setBusinessLicense(String businessLicense) {
        this.licenseNumber = businessLicense;
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

    public List<String> getOperatingDays() {
        return operatingDays;
    }

    public void setOperatingDays(List<String> operatingDays) {
        this.operatingDays = operatingDays;
    }

    public String getBusinessRegNumber() {
        return registrationNumber;
    }

    public void setBusinessRegNumber(String businessRegNumber) {
        this.registrationNumber = businessRegNumber;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmailAddress) {
        this.businessEmail = businessEmailAddress;
    }

    public String getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(String serviceCategories) {
        this.serviceCategories = serviceCategories;
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

    public String getPhysicalBusinessAddress() {
        return physicalAddress;
    }

    public void setPhysicalBusinessAddress(String physicalBusinessAddress) {
        this.physicalAddress = physicalBusinessAddress;
    }

    public String getBusinessPhoneNumber() {
        return phoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.phoneNumber = businessPhoneNumber;
    }

    public Integer getYearsInOperation() {
        return yearsInOperation;
    }

    public void setYearsInOperation(Integer yearsInOperation) {
        this.yearsInOperation = yearsInOperation;
    }

    public Integer getMpesaPayBill() {
        return mpesaPayBill;
    }

    public void setMpesaPayBill(Integer mpesaPayBill) {
        this.mpesaPayBill = mpesaPayBill;
    }

    public Integer getMpesaTill() {
        return mpesaTill;
    }

    public void setMpesaTill(Integer mpesaTill) {
        this.mpesaTill = mpesaTill;
    }
}
