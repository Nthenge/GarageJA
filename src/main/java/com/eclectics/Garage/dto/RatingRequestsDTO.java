package com.eclectics.Garage.dto;

public class RatingRequestsDTO {

    private Integer stars;
    private String feedback;
    private Long garageId;
    private Long mechanicId;

    public RatingRequestsDTO() {
    }

    public RatingRequestsDTO(Integer stars, String feedback, Long garageId, Long mechanicId) {
        this.stars = stars;
        this.feedback = feedback;
        this.garageId = garageId;
        this.mechanicId = mechanicId;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
}
