package com.example.LibrodeRegistro.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users") // Nombre exacto de tu tabla
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // Getters y Setters CORRECTOS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Métodos requeridos por UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // O implementa roles si los tienes
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cambiar si implementas expiración de cuentas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cambiar si implementas bloqueo de cuentas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Cambiar si implementas expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        return true; // Cambiar si implementas desactivación de cuentas
    }
}
