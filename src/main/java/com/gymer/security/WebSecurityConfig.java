package com.gymer.security;

import com.gymer.security.common.filter.JWTAuthorizationFilter;
import com.gymer.security.login.JsonAuthenticationFilter;
import com.gymer.security.common.handler.JsonLogoutSuccessHandler;
import com.gymer.security.common.handler.LoginFailureHandler;
import com.gymer.security.login.LoginSuccessHandler;
import com.gymer.security.login.LoginService;
import com.gymer.security.oauth2.OAuth2SuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JsonLogoutSuccessHandler logoutSuccessHandler;
    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;
    private final LoginService loginService;
    private final Environment environment;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter())
                .addFilter(authorizationFilter())
                .logout()
                .logoutUrl("/api/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutSuccessUrl(environment.getProperty("server.address.frontend"))
                .and()
                .oauth2Login()
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(failureHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String frontUrl = environment.getProperty("server.address.frontend");
        configuration.setAllowedOrigins(Collections.singletonList(frontUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept", "X-Requested-With", "remember-me", "Authorization"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JWTAuthorizationFilter authorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(super.authenticationManager());
    }

    @Bean
    public JsonAuthenticationFilter authenticationFilter() throws Exception {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter();
        filter.setFilterProcessesUrl("/api/login");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

}
