package com.gymer.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.security.login.entity.LoginDetails;
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
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

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
                setCredentials(details);
            }
            areCredentialsEmpty(username, password);

            if (!credentialService.isCredentialExistsByEmail(username)) {
                throw new AuthenticationCredentialsNotFoundException("Username or password is empty.");
            }

            Credential credential = credentialService.getCredentialByEmail(username);

            if (!passwordEncoder.matches(password, credential.getPassword())) {
                throw new AuthenticationCredentialsNotFoundException("Username or password is empty.");
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

    private void setCredentials(LoginDetails details) {
        username = details.getEmail();
        username = username != null ? username : "";
        username = username.trim();
        password = details.getPassword();
        password = password != null ? password : "";
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
