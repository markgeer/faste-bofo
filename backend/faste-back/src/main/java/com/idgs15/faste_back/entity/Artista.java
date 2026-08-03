package com.idgs15.faste_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artistas")
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false, length = 150)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;
    
    @Column(length = 100)
    private String pais;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(name = "usuario_creador_id", nullable = false)
    private Integer usuarioCreadorId;
    
    private Boolean activo = true;
}