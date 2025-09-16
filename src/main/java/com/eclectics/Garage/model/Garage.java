package com.eclectics.Garage.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garages")
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] businessLicense;
    @Lob
    private byte[] professionalCertificate;
    @Lob
    private byte[] facilityPhotos;

    @Column(unique = true, nullable = false)
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

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    public Garage(Integer mpesaTill, Integer mpesaPayBill, User user, String businessPhoneNumber, String physicalBusinessAddress, String businessName, String specialisedServices, String serviceCategories, String twentyFourHours, Long operatingHours, Integer yearsInOperation, String businessEmailAddress, String businessRegNumber, Long garageId, byte[] facilityPhotos, byte[] professionalCertificate, byte[] businessLicense, Long id) {
        this.mpesaTill = mpesaTill;
        this.mpesaPayBill = mpesaPayBill;
        this.businessPhoneNumber = businessPhoneNumber;
        this.physicalBusinessAddress = physicalBusinessAddress;
        this.businessName = businessName;
        this.specialisedServices = specialisedServices;
        this.serviceCategories = serviceCategories;
        this.twentyFourHours = twentyFourHours;
        this.operatingHours = operatingHours;
        this.yearsInOperation = yearsInOperation;
        this.businessEmailAddress = businessEmailAddress;
        this.businessRegNumber = businessRegNumber;
        this.garageId = garageId;
        this.facilityPhotos = facilityPhotos;
        this.professionalCertificate = professionalCertificate;
        this.businessLicense = businessLicense;
        this.id = id;
        this.user = user;
    }
    @Transient
    public boolean isComplete() {
        return businessLicense != null && businessLicense.length > 0
                && professionalCertificate != null && professionalCertificate.length > 0
                && facilityPhotos != null && facilityPhotos.length > 0
                && garageId != null
                && operatingHours != null
                && businessRegNumber != null && !businessRegNumber.isBlank()
                && businessEmailAddress != null && !businessEmailAddress.isBlank()
                && twentyFourHours != null && !twentyFourHours.isBlank()
                && serviceCategories != null && !serviceCategories.isBlank()
                && specialisedServices != null && !specialisedServices.isBlank()
                && businessName != null && !businessName.isBlank()
                && physicalBusinessAddress != null && !physicalBusinessAddress.isBlank()
                && businessPhoneNumber != null && !businessPhoneNumber.isBlank()
                && yearsInOperation != null
                && (mpesaPayBill != null || mpesaTill != null) // at least one payment method
                && user != null; // must be linked to a user
    }

    public Garage() {}

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public byte[] getBusinessLicense() { return businessLicense;}
    public void setBusinessLicense(byte[] businessLicense) { this.businessLicense = businessLicense;}

    public byte[] getProfessionalCertificate() { return professionalCertificate;}
    public void setProfessionalCertificate(byte[] professionalCertificate) {this.professionalCertificate = professionalCertificate;}

    public byte[] getFacilityPhotos() { return facilityPhotos;}
    public void setFacilityPhotos(byte[] facilityPhotos) {this.facilityPhotos = facilityPhotos;}

    public Long getGarageId() { return garageId;}
    public void setGarageId(Long garageId) { this.garageId = garageId;}

    public String getBusinessRegNumber() {return businessRegNumber;}
    public void setBusinessRegNumber(String businessRegNumber) { this.businessRegNumber = businessRegNumber;}

    public String getBusinessEmailAddress() { return businessEmailAddress;}
    public void setBusinessEmailAddress(String businessEmailAddress) {this.businessEmailAddress = businessEmailAddress;}

    public Integer getYearsInOperation() { return yearsInOperation;}
    public void setYearsInOperation(Integer yearsInOperation) { this.yearsInOperation = yearsInOperation;}

    public Long getOperatingHours() { return operatingHours;}
    public void setOperatingHours(Long operatingHours) { this.operatingHours = operatingHours;}

    public String getTwentyFourHours() { return twentyFourHours;}
    public void setTwentyFourHours(String twentyFourHours) { this.twentyFourHours = twentyFourHours;}

    public String getServiceCategories() { return serviceCategories; }
    public void setServiceCategories(String serviceCategories) {this.serviceCategories = serviceCategories;}

    public String getSpecialisedServices() { return specialisedServices;}
    public void setSpecialisedServices(String specialisedServices) {this.specialisedServices = specialisedServices;}

    public String getBusinessName() { return businessName;}
    public void setBusinessName(String businessName) { this.businessName = businessName;}

    public String getPhysicalBusinessAddress() { return physicalBusinessAddress;}
    public void setPhysicalBusinessAddress(String physicalBusinessAddress) {this.physicalBusinessAddress = physicalBusinessAddress;}

    public String getBusinessPhoneNumber() { return businessPhoneNumber;}
    public void setBusinessPhoneNumber(String businessPhoneNumber) {this.businessPhoneNumber = businessPhoneNumber;}

    public Integer getMpesaPayBill() { return mpesaPayBill;}
    public void setMpesaPayBill(Integer mpesaPayBill) { this.mpesaPayBill = mpesaPayBill;}

    public Integer getMpesaTill() { return mpesaTill;}
    public void setMpesaTill(Integer mpesaTill) {this.mpesaTill = mpesaTill;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}
