package com.idgs15.faste_back.repository;

import com.idgs15.faste_back.entity.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObraRepository extends JpaRepository<Obra, Integer> {
    List<Obra> findByArtistaId(Integer artistaId);
}