package com.eclectics.Garage.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "garages")
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String licenseNumber;
    private String professionalCertificate;

    private String facilityPhotos;

    @Column(unique = true, nullable = false)
    private Long garageId;
    private List<String> operatingDays;

    private String registrationNumber;
    private String businessEmail;
    private String closingTime;
    private String openingTime;

    private List<Long> services;

    private String businessName;
    private String physicalAddress;
    @Embedded
    private Location businessLocation;
    private String phoneNumber;

    private Integer yearsInOperation;

    private Integer paybillNumber;
    private Integer accountNumber;
    private Integer mpesaTill;

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ServiceRequest> requests = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "garage_services",
            joinColumns = @JoinColumn(
                    name = "garage_id"
//                    referencedColumnName = "garageId"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "service_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Service> offeredServices = new HashSet<>();


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    public Garage(String businessLicense,Integer accountNumber, String professionalCertificate, Location businessLocation,String closingTime, String openingTime, String facilityPhotos, Long garageId, List<String> operatingDays, String businessRegNumber, String businessEmail, List<Long> services, String businessName, String physicalAddress, String phoneNumber, Integer yearsInOperation, Integer paybillNumber, Integer mpesaTill, List<ServiceRequest> requests, Set<Service> offeredServices, User user) {
        this.licenseNumber = businessLicense;
        this.professionalCertificate = professionalCertificate;
        this.facilityPhotos = facilityPhotos;
        this.accountNumber = accountNumber;
        this.garageId = garageId;
        this.operatingDays = operatingDays;
        this.registrationNumber = businessRegNumber;
        this.businessEmail = businessEmail;
        this.services = services;
        this.closingTime = closingTime;
        this.openingTime = openingTime;
        this.businessName = businessName;
        this.physicalAddress = physicalAddress;
        this.businessLocation = businessLocation;
        this.phoneNumber = phoneNumber;
        this.yearsInOperation = yearsInOperation;
        this.paybillNumber = paybillNumber;
        this.mpesaTill = mpesaTill;
        this.requests = requests;
        this.offeredServices = offeredServices;
        this.user = user;
    }

    @Transient
    public boolean isComplete() {
        return getMissingFields().isEmpty();
    }

    @Transient
    public List<String> getMissingFields() {
        List<String> missingFields = new ArrayList<>();

        if (licenseNumber == null || licenseNumber.isEmpty()) missingFields.add("businessLicense");
        if (operatingDays == null) missingFields.add("operatingDays");
        if (registrationNumber == null || registrationNumber.isBlank()) missingFields.add("businessRegNumber");
        if (businessEmail == null || businessEmail.isBlank()) missingFields.add("businessEmailAddress");
        if (services == null || services.isEmpty()) missingFields.add("specialisedServices");
        if (businessName == null || businessName.isBlank()) missingFields.add("businessName");
        if (physicalAddress == null || physicalAddress.isBlank()) missingFields.add("physicalBusinessAddress");
        if (phoneNumber == null || phoneNumber.isBlank()) missingFields.add("businessPhoneNumber");
        if (paybillNumber == null) missingFields.add("mpesaPayBill/mpesaTill");
        if (openingTime == null) missingFields.add("openingTime");
        if (closingTime == null) missingFields.add("closingTime");
        if (accountNumber == null) missingFields.add("accountNumber");

        return missingFields;
    }


    public Garage() {}

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getProfessionalCertificate() { return professionalCertificate;}
    public void setProfessionalCertificate(String professionalCertificate) {this.professionalCertificate = professionalCertificate;}

    public String getFacilityPhotos() { return facilityPhotos;}
    public void setFacilityPhotos(String facilityPhotos) {this.facilityPhotos = facilityPhotos;}

    public Long getGarageId() { return garageId;}
    public void setGarageId(Long garageId) { this.garageId = garageId;}

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBusinessEmail() { return businessEmail;}
    public void setBusinessEmail(String businessEmailAddress) {this.businessEmail = businessEmailAddress;}

    public Integer getYearsInOperation() { return yearsInOperation;}
    public void setYearsInOperation(Integer yearsInOperation) { this.yearsInOperation = yearsInOperation;}

    public List<String> getOperatingDays() { return operatingDays;}
    public void setOperatingDays(List<String> operatingDays) { this.operatingDays = operatingDays;}

    public List<Long> getServices() { return services;}
    public void setServices(List<Long> services) {this.services = services;}

    public String getBusinessName() { return businessName;}
    public void setBusinessName(String businessName) { this.businessName = businessName;}

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public Location getBusinessLocation() {
        return businessLocation;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBusinessLocation(Location businessLocation) {
        this.businessLocation = businessLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<ServiceRequest> getRequests() {return requests;
    }
    public void setRequests(List<ServiceRequest> requests) {
        this.requests = requests;
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

    public Set<Service> getOfferedServices() {
        return offeredServices;
    }
    public void setOfferedServices(Set<Service> offeredServices) {
        this.offeredServices = offeredServices;
    }

    public Integer getPaybillNumber() {
        return paybillNumber;
    }

    public void setPaybillNumber(Integer paybillNumber) {
        this.paybillNumber = paybillNumber;
    }

    public Integer getMpesaTill() { return mpesaTill;}
    public void setMpesaTill(Integer mpesaTill) {this.mpesaTill = mpesaTill;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

}
