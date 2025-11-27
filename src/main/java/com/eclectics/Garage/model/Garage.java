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
    @Lob
    private String businessLicense;
    @Lob
    private String professionalCertificate;
    @Lob
    private String facilityPhotos;

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

    public Garage(String businessLicense, String professionalCertificate, String facilityPhotos, Long garageId, Long operatingHours, String businessRegNumber, String businessEmailAddress, String twentyFourHours, String serviceCategories, String specialisedServices, String businessName, String physicalBusinessAddress, String businessPhoneNumber, Integer yearsInOperation, Integer mpesaPayBill, Integer mpesaTill, List<ServiceRequest> requests, Set<Service> offeredServices, User user) {
        this.businessLicense = businessLicense;
        this.professionalCertificate = professionalCertificate;
        this.facilityPhotos = facilityPhotos;
        this.garageId = garageId;
        this.operatingHours = operatingHours;
        this.businessRegNumber = businessRegNumber;
        this.businessEmailAddress = businessEmailAddress;
        this.twentyFourHours = twentyFourHours;
        this.serviceCategories = serviceCategories;
        this.specialisedServices = specialisedServices;
        this.businessName = businessName;
        this.physicalBusinessAddress = physicalBusinessAddress;
        this.businessPhoneNumber = businessPhoneNumber;
        this.yearsInOperation = yearsInOperation;
        this.mpesaPayBill = mpesaPayBill;
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

        if (businessLicense == null || businessLicense.isEmpty()) missingFields.add("businessLicense");
        if (professionalCertificate == null || professionalCertificate.isEmpty()) missingFields.add("professionalCertificate");
        if (facilityPhotos == null || facilityPhotos.isEmpty()) missingFields.add("facilityPhotos");
        if (operatingHours == null) missingFields.add("operatingHours");
        if (businessRegNumber == null || businessRegNumber.isBlank()) missingFields.add("businessRegNumber");
        if (businessEmailAddress == null || businessEmailAddress.isBlank()) missingFields.add("businessEmailAddress");
        if (specialisedServices == null || specialisedServices.isBlank()) missingFields.add("specialisedServices");
        if (businessName == null || businessName.isBlank()) missingFields.add("businessName");
        if (physicalBusinessAddress == null || physicalBusinessAddress.isBlank()) missingFields.add("physicalBusinessAddress");
        if (businessPhoneNumber == null || businessPhoneNumber.isBlank()) missingFields.add("businessPhoneNumber");
        if (mpesaPayBill == null && mpesaTill == null) missingFields.add("mpesaPayBill/mpesaTill");

        return missingFields;
    }


    public Garage() {}

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getBusinessLicense() { return businessLicense;}
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense;}

    public String getProfessionalCertificate() { return professionalCertificate;}
    public void setProfessionalCertificate(String professionalCertificate) {this.professionalCertificate = professionalCertificate;}

    public String getFacilityPhotos() { return facilityPhotos;}
    public void setFacilityPhotos(String facilityPhotos) {this.facilityPhotos = facilityPhotos;}

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

    public List<ServiceRequest> getRequests() {return requests;
    }
    public void setRequests(List<ServiceRequest> requests) {
        this.requests = requests;
    }

    public Set<Service> getOfferedServices() {
        return offeredServices;
    }
    public void setOfferedServices(Set<Service> offeredServices) {
        this.offeredServices = offeredServices;
    }

    public Integer getMpesaPayBill() { return mpesaPayBill;}
    public void setMpesaPayBill(Integer mpesaPayBill) { this.mpesaPayBill = mpesaPayBill;}

    public Integer getMpesaTill() { return mpesaTill;}
    public void setMpesaTill(Integer mpesaTill) {this.mpesaTill = mpesaTill;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

}
