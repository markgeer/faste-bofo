package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.RetoDiarioRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.entity.*;
import com.idgs15.faste_back.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // 🔐 TODOS los endpoints requieren rol ADMIN
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final RetoRepository retoRepository;
    private final RetoDiarioRepository retoDiarioRepository;
    private final RetoCumplidoRepository retoCumplidoRepository;

    public AdminController(UsuarioRepository usuarioRepository, 
                           ArtistaRepository artistaRepository,
                           RetoRepository retoRepository,
                           RetoDiarioRepository retoDiarioRepository,
                           RetoCumplidoRepository retoCumplidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
        this.retoRepository = retoRepository;
        this.retoDiarioRepository = retoDiarioRepository;
        this.retoCumplidoRepository = retoCumplidoRepository;
    }

    // ==================== USUARIOS ====================
    
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<ApiResponse> cambiarRol(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String nuevoRol = body.get("rol");
        if (nuevoRol == null || (!nuevoRol.equals("user") && !nuevoRol.equals("admin"))) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Rol inválido. Debe ser 'user' o 'admin'"));
        }
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new ApiResponse(true, "Rol actualizado a: " + nuevoRol));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse> eliminarUsuario(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado"));
    }

    // ==================== ARTISTAS ====================

    @GetMapping("/artistas")
    public ResponseEntity<List<Artista>> getArtistas() {
        return ResponseEntity.ok(artistaRepository.findAll());
    }

    @GetMapping("/artistas/{id}")
    public ResponseEntity<Artista> getArtistaById(@PathVariable Integer id) {
        return artistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/artistas/{id}")
    public ResponseEntity<ApiResponse> actualizarArtista(@PathVariable Integer id, @RequestBody Artista artistaActualizado) {
        return artistaRepository.findById(id)
                .map(artista -> {
                    artista.setNombre(artistaActualizado.getNombre());
                    artista.setBio(artistaActualizado.getBio());
                    artista.setImagenUrl(artistaActualizado.getImagenUrl());
                    artista.setPais(artistaActualizado.getPais());
                    artistaRepository.save(artista);
                    return ResponseEntity.ok(new ApiResponse(true, "Artista actualizado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/artistas/{id}")
    public ResponseEntity<ApiResponse> eliminarArtista(@PathVariable Integer id) {
        if (!artistaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        artistaRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Artista eliminado permanentemente"));
    }

    @PutMapping("/artistas/{id}/restaurar")
    public ResponseEntity<ApiResponse> restaurarArtista(@PathVariable Integer id) {
        return artistaRepository.findById(id)
                .map(artista -> {
                    artista.setActivo(true);
                    artistaRepository.save(artista);
                    return ResponseEntity.ok(new ApiResponse(true, "Artista restaurado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== RETOS ====================

    @GetMapping("/retos")
    public ResponseEntity<List<Reto>> getRetos() {
        return ResponseEntity.ok(retoRepository.findAll());
    }

    @GetMapping("/retos/{id}")
    public ResponseEntity<Reto> getRetoById(@PathVariable Integer id) {
        return retoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/retos/{id}")
    public ResponseEntity<ApiResponse> actualizarReto(@PathVariable Integer id, @RequestBody Reto retoActualizado) {
        return retoRepository.findById(id)
                .map(reto -> {
                    reto.setTitulo(retoActualizado.getTitulo());
                    reto.setDescripcion(retoActualizado.getDescripcion());
                    reto.setCategoria(retoActualizado.getCategoria());
                    reto.setDificultad(retoActualizado.getDificultad());
                    reto.setTiempoEstimado(retoActualizado.getTiempoEstimado());
                    retoRepository.save(reto);
                    return ResponseEntity.ok(new ApiResponse(true, "Reto actualizado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/retos/{id}")
    public ResponseEntity<ApiResponse> eliminarReto(@PathVariable Integer id) {
        if (!retoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        retoRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reto eliminado"));
    }

    // ==================== RETOS DIARIOS ====================

    @GetMapping("/retos-diarios")
    public ResponseEntity<List<RetoDiario>> getRetosDiarios() {
        return ResponseEntity.ok(retoDiarioRepository.findAll());
    }

    @GetMapping("/retos-diarios/usuario/{usuarioId}")
    public ResponseEntity<List<RetoDiario>> getRetosDiariosByUsuario(@PathVariable Integer usuarioId) {
        // Usamos findAll() y filtramos en memoria porque no tenemos método en repository
        List<RetoDiario> retos = retoDiarioRepository.findAll().stream()
                .filter(rd -> rd.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(retos);
    }

    @GetMapping("/retos-diarios/fecha")
    public ResponseEntity<List<RetoDiario>> getRetosDiariosByFecha(@RequestParam String fecha) {
        LocalDate fechaAsignacion = LocalDate.parse(fecha);
        // Usamos findAll() y filtramos en memoria
        List<RetoDiario> retos = retoDiarioRepository.findAll().stream()
                .filter(rd -> rd.getFechaAsignacion().equals(fechaAsignacion))
                .collect(Collectors.toList());
        return ResponseEntity.ok(retos);
    }

    @PostMapping("/retos-diarios")
    public ResponseEntity<ApiResponse> asignarRetoDiario(@RequestBody RetoDiarioRequest request) {
        // Verificar que el reto existe
        if (!retoRepository.existsById(request.getRetoId())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "El reto no existe"));
        }
        // Verificar que el usuario existe
        if (!usuarioRepository.existsById(request.getUsuarioId())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "El usuario no existe"));
        }

        RetoDiario retoDiario = new RetoDiario();
        retoDiario.setRetoId(request.getRetoId());
        retoDiario.setUsuarioId(request.getUsuarioId());
        retoDiario.setFechaAsignacion(request.getFechaAsignacion() != null ? request.getFechaAsignacion() : LocalDate.now());
        retoDiario.setCompletado(false);

        retoDiarioRepository.save(retoDiario);
        return ResponseEntity.ok(new ApiResponse(true, "Reto diario asignado correctamente"));
    }

    @DeleteMapping("/retos-diarios/{id}")
    public ResponseEntity<ApiResponse> eliminarRetoDiario(@PathVariable Integer id) {
        if (!retoDiarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        retoDiarioRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reto diario eliminado"));
    }

    // ==================== RETOS CUMPLIDOS ====================

    @GetMapping("/retos-cumplidos")
    public ResponseEntity<List<RetoCumplido>> getRetosCumplidos() {
        return ResponseEntity.ok(retoCumplidoRepository.findAll());
    }

    @DeleteMapping("/retos-cumplidos/{id}")
    public ResponseEntity<ApiResponse> eliminarRetoCumplido(@PathVariable Integer id) {
        if (!retoCumplidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        retoCumplidoRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Reto cumplido eliminado"));
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard/resumen")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        long totalUsuarios = usuarioRepository.count();
        long totalArtistas = artistaRepository.count();
        long totalRetos = retoRepository.count();
        long totalRetosCumplidos = retoCumplidoRepository.count();
        
        // Retos completados hoy
        long retosCompletadosHoy = retoDiarioRepository.findAll().stream()
                .filter(rd -> rd.getFechaAsignacion().equals(LocalDate.now()) && rd.getCompletado())
                .count();
        
        // Usuarios activos (con al menos un reto cumplido)
        long usuariosActivos = retoCumplidoRepository.findAll().stream()
                .map(RetoCumplido::getUsuarioId)
                .distinct()
                .count();

        dashboard.put("totalUsuarios", totalUsuarios);
        dashboard.put("totalArtistas", totalArtistas);
        dashboard.put("totalRetos", totalRetos);
        dashboard.put("totalRetosCumplidos", totalRetosCumplidos);
        dashboard.put("retosCompletadosHoy", retosCompletadosHoy);
        dashboard.put("usuariosActivos", usuariosActivos);

        return ResponseEntity.ok(dashboard);
    }
}