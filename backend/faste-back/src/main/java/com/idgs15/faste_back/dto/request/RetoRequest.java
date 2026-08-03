package com.idgs15.faste_back.dto.request;

import lombok.Data;

@Data
public class RetoRequest {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String dificultad;
    private Integer tiempoEstimado;
}