package com.eclectics.Garage.dto;

public class UserEmailConfirmationDTO {
    private String token;
    private String email;

    public UserEmailConfirmationDTO() {
    }

    public UserEmailConfirmationDTO(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
