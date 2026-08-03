package com.idgs15.faste_back.repository;

import com.idgs15.faste_back.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArtistaRepository extends JpaRepository<Artista, Integer> {
    Optional<Artista> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}