package com.idgs15.faste_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "retos_cumplidos")
public class RetoCumplido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reto_diario_id", nullable = false)
    private Integer retoDiarioId;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida = LocalDateTime.now();
    
    private Integer likes = 0;
}