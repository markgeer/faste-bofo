package com.idgs15.faste_back.repository;

import com.idgs15.faste_back.entity.RetoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface RetoDiarioRepository extends JpaRepository<RetoDiario, Integer> {
    Optional<RetoDiario> findByUsuarioIdAndFechaAsignacion(Integer usuarioId, LocalDate fecha);
}