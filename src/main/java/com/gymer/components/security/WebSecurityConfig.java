package com.gymer.components.security;

import com.gymer.components.login.LoginService;
import com.gymer.components.security.common.filter.CORSFilter;
import com.gymer.components.security.common.filter.JsonAuthenticationFilter;
import com.gymer.components.security.common.handler.JsonLogoutSuccessHandler;
import com.gymer.components.security.common.handler.LoginFailureHandler;
import com.gymer.components.security.common.handler.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JsonLogoutSuccessHandler logoutSuccessHandler;
    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;
    private final LoginService loginService;
    private final String frontUrl;

    public WebSecurityConfig(JsonLogoutSuccessHandler logoutSuccessHandler, LoginSuccessHandler successHandler,
                             LoginFailureHandler failureHandler,
                             LoginService loginService, Environment environment) {
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.loginService = loginService;
        this.frontUrl = environment.getProperty("server.address.frontend");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/js/**", "/css/**", "/img/**").permitAll()
                .antMatchers(HttpMethod.GET, "/me/**", "/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/login", "/registration/**").anonymous()

                .antMatchers(HttpMethod.POST, "/api/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()

                .antMatchers("/slotuser/**").permitAll()
                .antMatchers("/slotemployee/**").authenticated()
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .and()
                .logout()
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutSuccessUrl(frontUrl);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(getPasswordEncoder());
        auth.setUserDetailsService(loginService);
        return auth;
    }

    @Bean
    public Filter getCustomCORSFilter() {
        return new CORSFilter();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JsonAuthenticationFilter authenticationFilter() throws Exception {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

}
