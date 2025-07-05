package com.example.LibrodeRegistro.security;

import com.example.LibrodeRegistro.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustonUserDetails implements UserDetails {

    private final User user;

    public CustonUserDetails(User user) {
        this.user = user;
    }

    // ✅ Método para acceder al usuario completo
    public User getUser() {
        return user;
    }

    // ✅ Implementaciones requeridas por UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // o tus roles
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // o el campo que usas como login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

