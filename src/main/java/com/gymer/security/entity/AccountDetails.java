package com.gymer.security.entity;

import com.gymer.api.credential.entity.Credential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AccountDetails implements UserDetails {

    private final Credential credential;

    public AccountDetails(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return credential.getPassword();
    }

    @Override
    public String getUsername() {
        return credential.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return credential.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return credential.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credential.isActive();
    }

    @Override
    public boolean isEnabled() {
        return credential.isActive();
    }

}
