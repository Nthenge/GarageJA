package com.eclectics.Garage.dto;

public class UserPasswordResetResponseDTO {
    private String message;

    public UserPasswordResetResponseDTO() {
    }

    public UserPasswordResetResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
