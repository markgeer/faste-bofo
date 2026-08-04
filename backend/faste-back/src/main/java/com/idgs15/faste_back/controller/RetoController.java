package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.RetoRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.Reto;
import com.idgs15.faste_back.repository.RetoRepository;
import com.idgs15.faste_back.repository.RetoDiarioRepository;
import com.idgs15.faste_back.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/retos")
public class RetoController {
    
    private final RetoRepository retoRepository;
    private final RetoDiarioRepository retoDiarioRepository;
    private final UsuarioRepository usuarioRepository;
    
    public RetoController(RetoRepository retoRepository, 
                          RetoDiarioRepository retoDiarioRepository,
                          UsuarioRepository usuarioRepository) {
        this.retoRepository = retoRepository;
        this.retoDiarioRepository = retoDiarioRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    // ✅ CUALQUIER USUARIO AUTENTICADO
    @GetMapping
    public List<Reto> getAll() {
        return retoRepository.findAll();
    }
    
    // ❌ SOLO ADMIN - Crear reto
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> create(@RequestBody RetoRequest request, Authentication auth) {
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Reto reto = new Reto();
        reto.setTitulo(request.getTitulo());
        reto.setDescripcion(request.getDescripcion());
        reto.setCategoria(request.getCategoria());
        reto.setDificultad(request.getDificultad());
        reto.setTiempoEstimado(request.getTiempoEstimado());
        reto.setUsuarioCreadorId(usuario.getId());
        
        retoRepository.save(reto);
        return ResponseEntity.ok(new ApiResponse(true, "Reto creado exitosamente"));
    }
    
    // ✅ CUALQUIER USUARIO AUTENTICADO
    @GetMapping("/diario")
    public ResponseEntity<?> getRetoDiario(Authentication auth) {
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        var reto = retoDiarioRepository.findByUsuarioIdAndFechaAsignacion(
            usuario.getId(), LocalDate.now());
        
        if (reto.isPresent()) {
            return ResponseEntity.ok(reto.get());
        }
        return ResponseEntity.ok(new ApiResponse(false, "No hay reto asignado para hoy"));
    }
    
    // ❌ SOLO ADMIN - Actualizar reto
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody RetoRequest request) {
        return retoRepository.findById(id)
                .map(reto -> {
                    reto.setTitulo(request.getTitulo());
                    reto.setDescripcion(request.getDescripcion());
                    reto.setCategoria(request.getCategoria());
                    reto.setDificultad(request.getDificultad());
                    reto.setTiempoEstimado(request.getTiempoEstimado());
                    retoRepository.save(reto);
                    return ResponseEntity.ok(new ApiResponse(true, "Reto actualizado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ❌ SOLO ADMIN - Eliminar reto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        if (!retoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        retoRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reto eliminado"));
    }
}