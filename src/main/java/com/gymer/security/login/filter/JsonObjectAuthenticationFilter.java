package com.gymer.security.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.security.login.entity.LoginDetails;
import lombok.SneakyThrows;
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
//
//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			BufferedReader reader = request.getReader();
//			StringBuilder sb = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//			LoginDetails authRequest = objectMapper.readValue(sb.toString(), LoginDetails.class);
//
//			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//					authRequest.getEmail(), authRequest.getPassword()
//			);
//			setDetails(request, token);
//			return this.getAuthenticationManager().authenticate(token);
//		} catch (IOException e) {
//			throw new IllegalArgumentException(e.getMessage());
//		}
//	}


//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest request,
//												HttpServletResponse response) throws AuthenticationException {
//		try {
//			// Get username & password from request (JSON) any way you like
//			LoginDetails authRequest = new ObjectMapper().readValue(request.getInputStream(), LoginDetails.class);
//
//			Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
//
//			return getAuthenticationManager().authenticate(auth);
//		} catch (Exception exp) {
//			throw new RuntimeException(exp);
//		}
//	}

//	@Override
//	protected void successfulAuthentication(HttpServletRequest request,
//											HttpServletResponse response, FilterChain chain, Authentication authResult)
//			throws IOException, ServletException {
//
//		if (logger.isDebugEnabled()) {
//			logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
//					+ authResult);
//		}
//
//		// custom code
//
//		SecurityContextHolder.getContext().setAuthentication(authResult);
//	}


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        super.unsuccessfulAuthentication(request, response, failed);
    }

    @SneakyThrows
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

                if (!sb.toString().contains("password") || !sb.toString().contains("email")) {
                    return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("", ""));
                }
                LoginDetails details = objectMapper.readValue(sb.toString(), LoginDetails.class);

                String username = details.getEmail();
                username = username != null ? username : "";
                username = username.trim();
                String password = details.getPassword();
                password = password != null ? password : "";
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                this.setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("", ""));
            }
        }
    }


}
