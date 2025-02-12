package com.example.user_service.user;

// import java.util.List;
import java.util.Collection;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.user_service.model.User;

import org.springframework.security.core.GrantedAuthority;

public class UserInfoDetails implements UserDetails {
    
    private String email;
    private String password;
    // private List<GrantedAuthority> authorities;

    public UserInfoDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        // this.authorities = List.of
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement your logic if you need this
    }
}
