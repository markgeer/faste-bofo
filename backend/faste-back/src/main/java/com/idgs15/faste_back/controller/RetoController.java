package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.RetoRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.Reto;
import com.idgs15.faste_back.repository.RetoRepository;
import com.idgs15.faste_back.repository.RetoDiarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/retos")
public class RetoController {
    
    private final RetoRepository retoRepository;
    private final RetoDiarioRepository retoDiarioRepository;
    
    public RetoController(RetoRepository retoRepository, RetoDiarioRepository retoDiarioRepository) {
        this.retoRepository = retoRepository;
        this.retoDiarioRepository = retoDiarioRepository;
    }
    
    @GetMapping
    public List<Reto> getAll() {
        return retoRepository.findAll();
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody RetoRequest request, Authentication auth) {
        Reto reto = new Reto();
        reto.setTitulo(request.getTitulo());
        reto.setDescripcion(request.getDescripcion());
        reto.setCategoria(request.getCategoria());
        reto.setDificultad(request.getDificultad());
        reto.setTiempoEstimado(request.getTiempoEstimado());
        reto.setUsuarioCreadorId(getUserId(auth));
        
        retoRepository.save(reto);
        return ResponseEntity.ok(new ApiResponse(true, "Reto creado exitosamente"));
    }
    
    @GetMapping("/diario")
    public ResponseEntity<?> getRetoDiario(Authentication auth) {
        var reto = retoDiarioRepository.findByUsuarioIdAndFechaAsignacion(
            getUserId(auth), LocalDate.now());
        
        if (reto.isPresent()) {
            return ResponseEntity.ok(reto.get());
        }
        return ResponseEntity.ok(new ApiResponse(false, "No hay reto asignado para hoy"));
    }
    
    private Integer getUserId(Authentication auth) {
        return 1; // Simplificado
    }
}