package com.gymer.accountlogin;

import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
class LoginService implements UserDetailsService {

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
