package com.idgs15.faste_back.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String email;
    private String nombre;
    private String rol;
}