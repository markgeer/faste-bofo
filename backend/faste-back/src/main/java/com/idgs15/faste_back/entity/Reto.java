package com.idgs15.faste_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "retos")
public class Reto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(length = 100)
    private String categoria;
    
    private String dificultad = "medio";
    
    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;
    
    @Column(name = "usuario_creador_id", nullable = false)
    private Integer usuarioCreadorId;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private Boolean activo = true;
}