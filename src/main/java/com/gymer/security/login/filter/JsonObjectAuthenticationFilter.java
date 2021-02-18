package com.gymer.security.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.security.login.entity.LoginDetails;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonObjectAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            try {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String username = "";
                String password = "";

                if (!sb.toString().contains("password") || !sb.toString().contains("email")) {
                    throw new AuthenticationCredentialsNotFoundException("Not found valid credentials.");
                }

                if (sb.toString().contains("password") && sb.toString().contains("email")) {
                    LoginDetails details = objectMapper.readValue(sb.toString(), LoginDetails.class);
                    username = details.getEmail();
                    username = username != null ? username : "";
                    username = username.trim();
                    password = details.getPassword();
                    password = password != null ? password : "";
                }

                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                this.setDetails(request, authRequest);
                setUsernameParameter(username);
                setPasswordParameter(password);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new AuthenticationCredentialsNotFoundException("Not found valid credentials.");
            }
        }
    }

}
