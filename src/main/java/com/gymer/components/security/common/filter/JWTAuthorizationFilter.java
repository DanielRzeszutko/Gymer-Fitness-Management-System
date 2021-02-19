package com.gymer.components.security.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    private Environment environment;

    @Autowired
    private CredentialService credentialService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null) {
            String userEmail = decodeJwtUserEmail(tokenHeader);
            return tryToGetAuthenticationToken(userEmail);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken tryToGetAuthenticationToken(String userEmail) {
        if (userEmail != null) {
            try {
                Credential credential = credentialService.getCredentialByEmail(userEmail);
                GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + credential.getRole());
                return new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));
            } catch (ResponseStatusException e) {
                return null;
            }
        }
        return null;
    }

    private String decodeJwtUserEmail(String tokenHeader) {
        String token = tokenHeader.substring(7);

        String secretKey = environment.getProperty("jwt.secret.password");
        secretKey = secretKey != null ? secretKey : "";

        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getSubject();
    }

}
