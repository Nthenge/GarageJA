package com.eclectics.Garage.dto;

public class GarageResponseDTO {
    private String businessLicense;
    private String professionalCertificate;
    private String facilityPhotos;

    private Long garageId;
    private Long operatingHours;

    private String businessRegNumber;
    private String businessEmailAddress;
    private String twentyFourHours;
    private String serviceCategories;
    private String specialisedServices;

    private String businessName;
    private String physicalBusinessAddress;
    private String businessPhoneNumber;

    private Integer yearsInOperation;
    private Integer mpesaPayBill;
    private Integer mpesaTill;

    public GarageResponseDTO() {}

    public GarageResponseDTO(String businessLicense, String professionalCertificate, String facilityPhotos, Long garageId, Long operatingHours, String businessRegNumber, String businessEmailAddress, String serviceCategories, String businessPhoneNumber, Integer mpesaPayBill, Integer yearsInOperation, String businessName, String specialisedServices, String twentyFourHours, String physicalBusinessAddress, Integer mpesaTill) {
        this.businessLicense = businessLicense;
        this.professionalCertificate = professionalCertificate;
        this.facilityPhotos = facilityPhotos;
        this.garageId = garageId;
        this.operatingHours = operatingHours;
        this.businessRegNumber = businessRegNumber;
        this.businessEmailAddress = businessEmailAddress;
        this.serviceCategories = serviceCategories;
        this.businessPhoneNumber = businessPhoneNumber;
        this.mpesaPayBill = mpesaPayBill;
        this.yearsInOperation = yearsInOperation;
        this.businessName = businessName;
        this.specialisedServices = specialisedServices;
        this.twentyFourHours = twentyFourHours;
        this.physicalBusinessAddress = physicalBusinessAddress;
        this.mpesaTill = mpesaTill;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
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

    public Long getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(Long operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getBusinessRegNumber() {
        return businessRegNumber;
    }

    public void setBusinessRegNumber(String businessRegNumber) {
        this.businessRegNumber = businessRegNumber;
    }

    public String getBusinessEmailAddress() {
        return businessEmailAddress;
    }

    public void setBusinessEmailAddress(String businessEmailAddress) {
        this.businessEmailAddress = businessEmailAddress;
    }

    public String getTwentyFourHours() {
        return twentyFourHours;
    }

    public void setTwentyFourHours(String twentyFourHours) {
        this.twentyFourHours = twentyFourHours;
    }

    public String getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(String serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    public String getSpecialisedServices() {
        return specialisedServices;
    }

    public void setSpecialisedServices(String specialisedServices) {
        this.specialisedServices = specialisedServices;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhysicalBusinessAddress() {
        return physicalBusinessAddress;
    }

    public void setPhysicalBusinessAddress(String physicalBusinessAddress) {
        this.physicalBusinessAddress = physicalBusinessAddress;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
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
