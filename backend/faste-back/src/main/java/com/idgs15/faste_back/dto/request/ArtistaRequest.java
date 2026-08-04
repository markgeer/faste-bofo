package com.idgs15.faste_back.dto.request;

import lombok.Data;

@Data
public class ArtistaRequest {
    private String nombre;
    private String bio;
    private String imagenUrl;
    private String pais;
    private String fechaNacimiento; // ✅ String para recibir formato "1907-07-06"
}