package com.gymer.components.security.common.entity;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccountDetails implements UserDetails {

    private final Credential credential;

    public AccountDetails(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Role role = credential.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        authorities.add(authority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.credential.getPassword();
    }

    @Override
    public String getUsername() {
        return credential.getEmail();
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
        return credential.isActivated();
    }

    public Credential getCredential() {
        return credential;
    }

}
