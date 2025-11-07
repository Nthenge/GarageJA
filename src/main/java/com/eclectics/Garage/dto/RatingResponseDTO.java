package com.eclectics.Garage.dto;

import java.time.LocalDateTime;

public class RatingResponseDTO {

    private Long id;
    private Integer stars;
    private String feedback;
    private Long garageId;
    private Long mechanicId;
    private Long requestId;
    private LocalDateTime createdAt;

    public RatingResponseDTO() {
    }

    public RatingResponseDTO(Long id, Integer stars, LocalDateTime createdAt, Long requestId, Long mechanicId, Long garageId, String feedback) {
        this.id = id;
        this.stars = stars;
        this.createdAt = createdAt;
        this.requestId = requestId;
        this.mechanicId = mechanicId;
        this.garageId = garageId;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public Long getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(Long mechanicId) {
        this.mechanicId = mechanicId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
