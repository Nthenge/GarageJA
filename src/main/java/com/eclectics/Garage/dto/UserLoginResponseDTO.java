package com.eclectics.Garage.dto;

import com.eclectics.Garage.model.Role;

public class UserLoginResponseDTO {

    private String token;
//    private String tokenType = "Bearer";
//    private Long userId;
//    private String email;
    private Role role;

    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(String token, Role role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

//    public String getTokenType() {
//        return tokenType;
//    }
//
//    public void setTokenType(String tokenType) {
//        this.tokenType = tokenType;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }

//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
