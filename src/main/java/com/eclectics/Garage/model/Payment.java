package com.eclectics.Garage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private Integer paymentId;
    private Double amount;
    private Long ownerId;
    private Long serviceId;
    private String createdAt;
    private String updatedAt;
    private String transactionRef;
    @Enumerated(EnumType.STRING)
    private PaymentCurrency currency;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private Double garageAmount; // optional
    private Double systemAmount;

    public Payment() {}

    public Payment(Integer paymentId, Double amount, Long ownerId, Long serviceId, String createdAt, String updatedAt, String transactionRef, PaymentCurrency currency, PaymentMethod paymentMethod, PaymentStatus paymentStatus, Double garageAmount, Double systemAmount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.ownerId = ownerId;
        this.serviceId = serviceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionRef = transactionRef;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.garageAmount = garageAmount;
        this.systemAmount = systemAmount;
    }

    public Integer getPaymentId() {return paymentId;}
    public void setPaymentId(Integer paymentId) {this.paymentId = paymentId;}

    public Double getAmount() {return amount;}
    public void setAmount(Double amount) {this.amount = amount;}

    public String getCreatedAt() {return createdAt;}
    public void setCreatedAt(String createdAt) {this.createdAt = createdAt;}

    public Long getOwnerId() {return ownerId;}
    public void setOwnerId(Long ownerId) {this.ownerId = ownerId;}

    public Long getServiceId() {return serviceId;}
    public void setServiceId(Long serviceId) {this.serviceId = serviceId;}

    public PaymentStatus getPaymentStatus() {return paymentStatus;}
    public void setPaymentStatus(PaymentStatus paymentStatus) {this.paymentStatus = paymentStatus;}

    public PaymentMethod getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(PaymentMethod paymentMethod) {this.paymentMethod = paymentMethod;}

    public String getTransactionRef() {return transactionRef;}
    public void setTransactionRef(String transactionRef) {this.transactionRef = transactionRef;}

    public String getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(String updatedAt) {this.updatedAt = updatedAt;}

    public PaymentCurrency getCurrency() {return currency;}
    public void setCurrency(PaymentCurrency currency) {this.currency = currency;}

    public Double getGarageAmount() { return garageAmount;}
    public void setGarageAmount(Double garageAmount) {this.garageAmount = garageAmount;}

    public Double getSystemAmount() {return systemAmount;}
    public void setSystemAmount(Double systemAmount) {this.systemAmount = systemAmount;}
}


