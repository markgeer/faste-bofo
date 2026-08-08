package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.Obra;
import com.idgs15.faste_back.repository.ArtistaRepository;
import com.idgs15.faste_back.repository.ObraRepository;
import com.idgs15.faste_back.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/obras")
public class ObraController {

    private final ObraRepository obraRepository;
    private final ArtistaRepository artistaRepository;
    private final UsuarioRepository usuarioRepository;

    public ObraController(ObraRepository obraRepository,
                          ArtistaRepository artistaRepository,
                          UsuarioRepository usuarioRepository) {
        this.obraRepository = obraRepository;
        this.artistaRepository = artistaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ===== OBTENER OBRAS POR ARTISTA (CUALQUIER USUARIO) =====
    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<Obra>> getObrasByArtista(@PathVariable Integer artistaId) {
        List<Obra> obras = obraRepository.findByArtistaId(artistaId);
        return ResponseEntity.ok(obras);
    }

    // ===== OBTENER TODAS LAS OBRAS (SOLO ADMIN) =====
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Obra>> getAllObras() {
        return ResponseEntity.ok(obraRepository.findAll());
    }

    // ===== CREAR OBRA PARA UN ARTISTA (SOLO ADMIN) =====
    @PostMapping("/asignar/{artistaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> asignarObra(
            @PathVariable Integer artistaId,
            @RequestBody Obra obra,
            Authentication auth) {

        // Verificar que el artista existe
        if (!artistaRepository.existsById(artistaId)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El artista no existe"));
        }

        // Obtener usuario autenticado
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asignar datos a la obra
        obra.setArtistaId(artistaId);
        obra.setUsuarioCreadorId(usuario.getId());

        Obra obraGuardada = obraRepository.save(obra);
        return ResponseEntity.ok(new ApiResponse(true, "Obra asignada exitosamente", obraGuardada));
    }

    // ===== ELIMINAR OBRA (SOLO ADMIN) =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> eliminarObra(@PathVariable Integer id) {
        if (!obraRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        obraRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Obra eliminada"));
    }
}