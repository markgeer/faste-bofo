package com.idgs15.faste_back.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}