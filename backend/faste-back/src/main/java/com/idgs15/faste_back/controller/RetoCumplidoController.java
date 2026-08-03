package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.entity.RetoCumplido;
import com.idgs15.faste_back.repository.RetoCumplidoRepository;
import com.idgs15.faste_back.repository.RetoDiarioRepository;
import com.idgs15.faste_back.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/retos-cumplidos")
public class RetoCumplidoController {
    
    private final RetoCumplidoRepository retoCumplidoRepository;
    private final RetoDiarioRepository retoDiarioRepository;
    private final UsuarioRepository usuarioRepository;
    
    public RetoCumplidoController(RetoCumplidoRepository retoCumplidoRepository, 
                                   RetoDiarioRepository retoDiarioRepository,
                                   UsuarioRepository usuarioRepository) {
        this.retoCumplidoRepository = retoCumplidoRepository;
        this.retoDiarioRepository = retoDiarioRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    @PostMapping
    public ResponseEntity<RetoCumplido> create(@RequestBody RetoCumplido cumplido, Authentication auth) {
        // ✅ OBTENER ID DEL USUARIO AUTENTICADO
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        cumplido.setUsuarioId(usuario.getId());
        
        // Marcar reto diario como completado
        var retoDiario = retoDiarioRepository.findById(cumplido.getRetoDiarioId())
                .orElseThrow(() -> new RuntimeException("Reto diario no encontrado"));
        retoDiario.setCompletado(true);
        retoDiarioRepository.save(retoDiario);
        
        // ✅ LOG PARA VERIFICAR
        System.out.println("✅ Reto cumplido guardado para usuario: " + usuario.getEmail());
        
        return ResponseEntity.ok(retoCumplidoRepository.save(cumplido));
    }
    
    @GetMapping("/usuario")
    public List<RetoCumplido> getByUsuario(Authentication auth) {
        String email = auth.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // ✅ FILTRAR POR USUARIO REAL
        return retoCumplidoRepository.findAll().stream()
                .filter(r -> r.getUsuarioId().equals(usuario.getId()))
                .toList();
    }
    
    // ✅ ENDPOINT PARA VER TODOS (solo admin)
    @GetMapping
    public List<RetoCumplido> getAll() {
        return retoCumplidoRepository.findAll();
    }
}