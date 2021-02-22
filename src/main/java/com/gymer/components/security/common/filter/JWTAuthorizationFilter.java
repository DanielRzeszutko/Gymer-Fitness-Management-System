package com.gymer.components.security.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.components.security.common.entity.TokenType;
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
import javax.servlet.http.Cookie;
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

    private final String TOKEN_HEADER_NAME = "Authorization";
    final String TOKEN_STARTER = "Bearer ";
    final String COOKIE_STARTER = "exo";

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String cookieToken = isTokenExistAndIfGetTokenFromCookie(request.getCookies());
        if (cookieToken == null || !cookieToken.startsWith(COOKIE_STARTER)) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader(TOKEN_HEADER_NAME);
        if (jwtToken == null || !jwtToken.startsWith(TOKEN_STARTER)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken cookieAuthentication = getAuthentication(cookieToken, TokenType.COOKIE);
        if (cookieAuthentication == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken jwtAuthentication = getAuthentication(jwtToken, TokenType.JWT);
        if (jwtAuthentication == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!authenticationUserEquals(cookieAuthentication, jwtAuthentication)) {
            chain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
        chain.doFilter(request, response);
    }

    private boolean authenticationUserEquals(UsernamePasswordAuthenticationToken cookieAuthentication,
                                             UsernamePasswordAuthenticationToken jwtAuthentication) {
        return (cookieAuthentication.getPrincipal()).equals(jwtAuthentication.getPrincipal());
    }

    private String isTokenExistAndIfGetTokenFromCookie(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_HEADER_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token, TokenType tokenType) {
        if (token == null) return null;
        String userEmail = decodeToken(token, tokenType);
        return tryToGetAuthenticationToken(userEmail);
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

    private String decodeToken(String token, TokenType tokenType) {
        if (tokenType.equals(TokenType.COOKIE)) {
            return decodeTokenFromCookie(token);
        }
        return decodeTokenFromJwt(token);
    }

    private String decodeTokenWithAlgorithm(String token, Algorithm algorithm) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getSubject();
    }

    private String decodeTokenFromCookie(String token) {
        token = token.substring(3);
        String cookieSecretKey = environment.getProperty("cookie.secret.password");
        cookieSecretKey = cookieSecretKey != null ? cookieSecretKey : "";
        Algorithm algorithm = Algorithm.HMAC384(cookieSecretKey);

        return decodeTokenWithAlgorithm(token, algorithm);
    }

    private String decodeTokenFromJwt(String token) {
        token = token.substring(7);
        String jwtSecretKey = environment.getProperty("jwt.secret.password");
        jwtSecretKey = jwtSecretKey != null ? jwtSecretKey : "";
        Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey);

        return decodeTokenWithAlgorithm(token, algorithm);
    }

}
