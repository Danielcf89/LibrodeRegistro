package com.example.LibrodeRegistro.service;

import com.example.LibrodeRegistro.dto.JwtResponse;
import com.example.LibrodeRegistro.dto.LoginRequest;
import com.example.LibrodeRegistro.dto.RegisterRequest;
import com.example.LibrodeRegistro.entity.User;
import com.example.LibrodeRegistro.repository.UserRepository;
import com.example.LibrodeRegistro.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return generateTokenResponse(user.getUsername());
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateTokenResponse(request.getUsername());
    }

    private JwtResponse generateTokenResponse(String username) {
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("") // No se necesita la contraseña para generar el token
                        .authorities("ROLE_USER")
                        .build());
        return new JwtResponse(token);
    }
}
