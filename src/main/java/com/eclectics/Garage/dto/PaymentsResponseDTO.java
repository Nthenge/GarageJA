package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.PaymentCurrency;
import com.eclectics.Garage.model.PaymentMethod;
import com.eclectics.Garage.model.PaymentStatus;

public class PaymentsResponseDTO {
    private Integer paymentId;
    private Double amount;
    private Long ownerId;
    private Long serviceId;
    private String updatedAt;
    private String transactionRef;
    private PaymentCurrency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    public PaymentsResponseDTO() {}

    public PaymentsResponseDTO(Integer paymentId, Double amount, Long ownerId, Long serviceId, String updatedAt, String transactionRef, PaymentCurrency currency, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.ownerId = ownerId;
        this.serviceId = serviceId;
        this.updatedAt = updatedAt;
        this.transactionRef = transactionRef;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public PaymentCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(PaymentCurrency currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
