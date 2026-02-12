package com.lovepaws.app.api.service.impl;

import com.lovepaws.app.api.dto.LoginRequestDTO;
import com.lovepaws.app.api.dto.LoginResponseDTO;
import com.lovepaws.app.api.exception.ApiUnauthorizedException;
import com.lovepaws.app.api.security.JwtTokenService;
import com.lovepaws.app.api.service.AuthApiService;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApiServiceImpl implements AuthApiService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .or(() -> usuarioRepository.findByCorreo(request.getUsername()))
                .orElseThrow(() -> new ApiUnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new ApiUnauthorizedException("Credenciales inválidas");
        }

        String role = usuario.getRol() != null ? usuario.getRol().getNombre() : "ADOPTANTE";
        return LoginResponseDTO.builder()
                .token(jwtTokenService.generateToken(usuario))
                .tokenType("Bearer")
                .expiresIn(jwtTokenService.getExpirationSeconds())
                .username(usuario.getUsername())
                .role(role)
                .build();
    }
}
