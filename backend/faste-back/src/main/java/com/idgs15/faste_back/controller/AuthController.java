package com.idgs15.faste_back.controller;

import com.idgs15.faste_back.dto.request.LoginRequest;
import com.idgs15.faste_back.dto.request.RegistroRequest;
import com.idgs15.faste_back.dto.response.ApiResponse;
import com.idgs15.faste_back.dto.response.AuthResponse;
import com.idgs15.faste_back.entity.Usuario;
import com.idgs15.faste_back.repository.UsuarioRepository;
import com.idgs15.faste_back.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse> registro(@RequestBody RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "El email ya está registrado"));
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setBio(request.getBio());
        
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario registrado exitosamente"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setRol(usuario.getRol());
        
        return ResponseEntity.ok(response);
    }
}