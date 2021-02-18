package com.gymer.components.common.security.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.components.login.entity.LoginDetails;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
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

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            setUsernameParameter(username);
            setPasswordParameter(password);
            return this.getAuthenticationManager().authenticate(authRequest);
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
