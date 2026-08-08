package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.entity.Usuario;
import com.idgs15.faste_back.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/perfil")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizarPerfil(
            Authentication auth,
            @RequestBody Map<String, String> datos) {

        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (datos.containsKey("nombre")) {
            usuario.setNombre(datos.get("nombre"));
        }
        if (datos.containsKey("bio")) {
            usuario.setBio(datos.get("bio"));
        }
        if (datos.containsKey("avatarUrl")) {
            usuario.setAvatarUrl(datos.get("avatarUrl"));
        }

        usuarioRepository.save(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Perfil actualizado exitosamente");
        return ResponseEntity.ok(response);
    }
}