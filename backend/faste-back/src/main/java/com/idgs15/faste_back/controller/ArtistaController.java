package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.ArtistaRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.Artista;
import com.idgs15.faste_back.entity.Obra;
import com.idgs15.faste_back.repository.ArtistaRepository;
import com.idgs15.faste_back.repository.UsuarioRepository;
import com.idgs15.faste_back.repository.ObraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {
    
    private final ArtistaRepository artistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObraRepository obraRepository;

    public ArtistaController(ArtistaRepository artistaRepository, UsuarioRepository usuarioRepository, ObraRepository obraRepository) {
        this.artistaRepository = artistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.obraRepository = obraRepository;
    }
    
    // ✅ CUALQUIER USUARIO AUTENTICADO (USER o ADMIN)
    @GetMapping
    public ResponseEntity<List<Artista>> getAllArtistas() {
        List<Artista> artistas = artistaRepository.findAll();
        return ResponseEntity.ok(artistas);
    }
    
    // ✅ CUALQUIER USUARIO AUTENTICADO
    @GetMapping("/{id}")
    public ResponseEntity<Artista> getArtistaById(@PathVariable Integer id) {
        return artistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ❌ SOLO ADMIN - Crear artista
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createArtista(@RequestBody ArtistaRequest request, Authentication auth) {
        if (artistaRepository.existsByNombre(request.getNombre())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El artista ya existe en la base de datos"));
        }
        
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Artista artista = new Artista();
        artista.setNombre(request.getNombre());
        artista.setBio(request.getBio());
        artista.setImagenUrl(request.getImagenUrl());
        artista.setPais(request.getPais());
        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()) {
            artista.setFechaNacimiento(request.getFechaNacimiento());
        }
        artista.setUsuarioCreadorId(usuario.getId());
        artista.setActivo(true);
        
        artistaRepository.save(artista);
        // ✅ DEVOLVER EL ID DEL ARTISTA CREADO
        return ResponseEntity.ok(new ApiResponse(true, "Artista creado exitosamente", artista.getId()));
    }
    
    // ❌ SOLO ADMIN - Actualizar artista
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateArtista(@PathVariable Integer id, 
                                                      @RequestBody ArtistaRequest request) {
        return artistaRepository.findById(id)
                .map(artista -> {
                    artista.setNombre(request.getNombre());
                    artista.setBio(request.getBio());
                    artista.setImagenUrl(request.getImagenUrl());
                    artista.setPais(request.getPais());
                    artistaRepository.save(artista);
                    return ResponseEntity.ok(new ApiResponse(true, "Artista actualizado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ❌ SOLO ADMIN - Eliminar artista
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteArtista(@PathVariable Integer id) {
        if (!artistaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        artistaRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Artista eliminado permanentemente"));
    }
    
    // ✅ CUALQUIER USUARIO AUTENTICADO
    @GetMapping("/buscar")
    public ResponseEntity<Artista> buscarPorNombre(@RequestParam String nombre) {
        return artistaRepository.findByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== ASIGNAR OBRA A ARTISTA (SOLO ADMIN) =====
    @PostMapping("/{id}/obras")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> asignarObra(
            @PathVariable Integer id,
            @RequestBody Obra obra,
            Authentication auth) {

        // Verificar que el artista existe
        if (!artistaRepository.existsById(id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El artista no existe"));
        }

        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        obra.setArtistaId(id);
        obra.setUsuarioCreadorId(usuario.getId());

        obraRepository.save(obra);
        return ResponseEntity.ok(new ApiResponse(true, "Obra asignada al artista exitosamente"));
    }
}