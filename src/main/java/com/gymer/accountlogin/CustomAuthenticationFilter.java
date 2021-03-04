package com.gymer.accountlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PasswordEncoder passwordEncoder;
    private final CredentialService credentialService;
    private String username = "";
    private String password = "";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {
            StringBuilder sb = getBuilder(request);
            isJsonFormValid(sb);

            if (sb.toString().contains("password") && sb.toString().contains("email")) {
                LoginDetails details = objectMapper.readValue(sb.toString(), LoginDetails.class);
                setCredentials(details.getEmail(), details.getPassword());
            }
            areCredentialsEmpty(username, password);

            if (!credentialService.isCredentialExistsByEmail(username)) {
                throw new AuthenticationCredentialsNotFoundException("Username or password is not valid.");
            }

            Credential credential = credentialService.getCredentialByEmail(username);

            if (!credentialService.isActivatedCredentialExistsByEmail(username)) {
                throw new AuthenticationCredentialsNotFoundException("Account not activated.");
            }

            if (!passwordEncoder.matches(password, credential.getPassword())) {
                throw new AuthenticationCredentialsNotFoundException("Username or password is not valid.");
            }

            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + credential.getRole());
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authRequest);
            return authRequest;
        } catch (IOException e) {
            throw new AuthenticationCredentialsNotFoundException("Unknown error, sorry. Please be patient.");
        }

    }

    private void isJsonFormValid(StringBuilder sb) {
        if (!sb.toString().contains("password") || !sb.toString().contains("email")) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JSON request format, " +
                    "fields needed: email and password.");
        }
    }

    private void setCredentials(String email, String password) {
        this.username = email;
        this.username = username != null ? username : "";
        this.username = username.trim();
        this.password = password != null ? password : "";
    }

    private void areCredentialsEmpty(String username, String password) {
        if (username.equals("") || password.equals("")) {
            throw new AuthenticationCredentialsNotFoundException("Username or password is empty.");
        }
    }

    private StringBuilder getBuilder(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb;
    }

}
