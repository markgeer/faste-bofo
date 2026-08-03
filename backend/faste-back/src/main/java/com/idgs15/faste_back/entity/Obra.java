package com.idgs15.faste_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "obras")
public class Obra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "artista_id", nullable = false)
    private Integer artistaId;
    
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;
    
    @Column(name = "año_creacion")
    private Integer añoCreacion;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(name = "usuario_creador_id", nullable = false)
    private Integer usuarioCreadorId;
}