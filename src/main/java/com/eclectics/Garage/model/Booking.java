package com.eclectics.Garage.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "garage_id")
    private Long garageId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "mechanic_id")
    private Long mechanicId;

    private LocalDateTime date;
    private String status;

    public Booking() {
    }

    public Booking(Long id, Long garageId, Long userId, Long serviceId, Long mechanicId, LocalDateTime date, String status) {
        this.id = id;
        this.garageId = garageId;
        this.userId = userId;
        this.serviceId = serviceId;
        this.mechanicId = mechanicId;
        this.date = date;
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public void setMechanicId(Long mechanicId) {
        this.mechanicId = mechanicId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGarageId() {
        return garageId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getMechanicId() {
        return mechanicId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }


}

