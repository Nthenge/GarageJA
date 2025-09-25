package com.eclectics.Garage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    private PaymentCurrency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    public Payment() {}

    public Payment(Double amount, Integer paymentId, String createdAt, String updatedAt, PaymentCurrency currency, Long ownerId, String transactionRef, PaymentMethod paymentMethod, PaymentStatus paymentStatus, Long serviceId) {
        this.amount = amount;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.currency = currency;
        this.ownerId = ownerId;
        this.transactionRef = transactionRef;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.serviceId = serviceId;
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
}


