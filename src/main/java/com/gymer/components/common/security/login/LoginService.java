package com.gymer.components.common.security.login;

import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.components.common.security.common.entity.AccountDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService implements UserDetailsService {

    private final CredentialService credentialService;

    public LoginService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Credential credential = credentialService.getCredentialByEmail(email);
            return new AccountDetails(credential);
        } catch (ResponseStatusException e) {
            throw new UsernameNotFoundException("Username not found.");
        }

    }

}
