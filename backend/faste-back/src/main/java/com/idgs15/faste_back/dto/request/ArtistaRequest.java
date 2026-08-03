package com.idgs15.faste_back.dto.request;

import lombok.Data;

@Data
public class ArtistaRequest {
    private String nombre;
    private String bio;
    private String imagenUrl;
    private String pais;
}