package com.gymer.accountlogin;

import com.gymer.common.resources.credential.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAndLogoutConfig {

    private final Environment environment;
    private final PasswordEncoder passwordEncoder;
    private final CredentialService credentialService;
    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomLoginFailureHandler loginFailureHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final LoginService loginService;

    public void configureLoginAndLogout(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http.addFilter(authenticationFilter(authenticationManager))
                .addFilter(authorizationFilter(authenticationManager))
                .logout()
                .logoutUrl("/api/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutSuccessUrl(environment.getProperty("server.address.frontend"));
    }

    public void configureLoginAndLogout(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder);
    }

    public CustomAuthorizationFilter authorizationFilter(AuthenticationManager authenticationManager) {
        return new CustomAuthorizationFilter(authenticationManager, environment, credentialService);
    }

    public CustomAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(passwordEncoder, credentialService);
        filter.setFilterProcessesUrl("/api/login");
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }


}
