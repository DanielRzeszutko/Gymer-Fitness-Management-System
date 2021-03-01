package com.gymer.components.security.login;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.components.security.common.entity.AccountDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class LoginService implements UserDetailsService {

    private final CredentialService credentialService;

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
