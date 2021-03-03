package com.gymer.common.security;

import com.gymer.accountlogin.LoginAndLogoutConfig;
import com.gymer.oauth2singlesignin.OAuth2Config;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginAndLogoutConfig loginAndLogoutConfig;
    private final OAuth2Config oAuth2Config;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http.requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/v2/**", "/js/**", "/css/**", "/img/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/me/**", "/api/logout", "/api/verify").permitAll()
                .antMatchers(HttpMethod.POST, "/api/login", "/api/registration/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/authorization").permitAll()

                .antMatchers(HttpMethod.GET, "/api/populate").permitAll()

                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .antMatchers("/api/slotuser/**").permitAll()
                .antMatchers("/api/slotemployee/**").authenticated()
                .anyRequest().authenticated();

        loginAndLogoutConfig.configureLoginAndLogout(http, super.authenticationManager());
        oAuth2Config.configureOAuth2(http);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        loginAndLogoutConfig.configureLoginAndLogout(auth);
    }

    @Bean
    AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }

}
