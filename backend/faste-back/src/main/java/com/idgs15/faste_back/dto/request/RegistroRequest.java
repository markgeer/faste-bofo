package com.idgs15.faste_back.dto.request;

import lombok.Data;

@Data
public class RegistroRequest {
    private String nombre;
    private String email;
    private String password;
    private String bio;
}