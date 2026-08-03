package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.ArtistaRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.Artista;
import com.idgs15.faste_back.repository.ArtistaRepository;
import com.idgs15.faste_back.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {
    
    private final ArtistaRepository artistaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public ArtistaController(ArtistaRepository artistaRepository, UsuarioRepository usuarioRepository) {
        this.artistaRepository = artistaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    // OBTENER TODOS LOS ARTISTAS
    @GetMapping
    public ResponseEntity<List<Artista>> getAllArtistas() {
        List<Artista> artistas = artistaRepository.findAll();
        return ResponseEntity.ok(artistas);
    }
    
    // OBTENER ARTISTA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Artista> getArtistaById(@PathVariable Integer id) {
        return artistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // CREAR ARTISTA
    @PostMapping
    public ResponseEntity<ApiResponse> createArtista(@RequestBody ArtistaRequest request, Authentication auth) {
        // Verificar si ya existe
        if (artistaRepository.existsByNombre(request.getNombre())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El artista ya existe en la base de datos"));
        }
        
        // Obtener usuario autenticado
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear artista
        Artista artista = new Artista();
        artista.setNombre(request.getNombre());
        artista.setBio(request.getBio());
        artista.setImagenUrl(request.getImagenUrl());
        artista.setPais(request.getPais());
        artista.setUsuarioCreadorId(usuario.getId());
        artista.setActivo(true);
        
        artistaRepository.save(artista);
        return ResponseEntity.ok(new ApiResponse(true, "Artista creado exitosamente"));
    }
    
    // ACTUALIZAR ARTISTA
    @PutMapping("/{id}")
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
    
    // ELIMINAR ARTISTA (DELETE FÍSICO - BORRA DE LA BD)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteArtista(@PathVariable Integer id) {
        if (!artistaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // ✅ BORRAR FÍSICAMENTE DE LA BD
        artistaRepository.deleteById(id);
        
        return ResponseEntity.ok(new ApiResponse(true, "Artista eliminado permanentemente"));
    }
    
    // BUSCAR ARTISTA POR NOMBRE
    @GetMapping("/buscar")
    public ResponseEntity<Artista> buscarPorNombre(@RequestParam String nombre) {
        return artistaRepository.findByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}