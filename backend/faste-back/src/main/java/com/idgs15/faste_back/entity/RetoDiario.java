package com.idgs15.faste_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "retos_diarios")
public class RetoDiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reto_id", nullable = false)
    private Integer retoId;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    private Boolean completado = false;
    
    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;
}