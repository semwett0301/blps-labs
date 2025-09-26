package com.example.code.security;

import com.example.code.filter.AuthorizationFilter;
import com.example.code.filter.UpdateTokenFilter;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public final static String[] PERMIT_ALL_PATHS = {"/auth/login", "/auth/register", "/auth/logout", "/auth/refresh", "/roles/**"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.csrf().disable();

        http.authorizeRequests().antMatchers(PERMIT_ALL_PATHS).permitAll();

        http.authorizeRequests().antMatchers(POST, "/orders/*/accept").hasAuthority(Role.COURIER.getRole());
        http.authorizeRequests().antMatchers(POST, "/orders/*/reject").hasAuthority(Role.COURIER.getRole());
        http.authorizeRequests().antMatchers(POST, "/orders/*/complete").hasAuthority(Role.COURIER.getRole());

        http.authorizeRequests().antMatchers(POST, "/orders").hasAuthority(Role.USER.getRole());
        http.authorizeRequests().antMatchers("/time/**").hasAuthority(Role.USER.getRole());
        http.authorizeRequests().antMatchers(GET, "/books").hasAuthority(Role.USER.getRole());

        http.authorizeRequests().antMatchers(GET, "/orders").hasAnyAuthority(Role.COURIER.getRole(), Role.USER.getRole());
        http.authorizeRequests().antMatchers(GET, "/orders/*").hasAnyAuthority(Role.COURIER.getRole(), Role.USER.getRole());
        http.authorizeRequests().antMatchers(POST, "/orders/cancel").hasAnyAuthority(Role.COURIER.getRole(), Role.USER.getRole());

        http.authorizeRequests().antMatchers(POST, "/users/**").hasAnyAuthority(Role.COURIER.getRole(), Role.USER.getRole());

        http.addFilterAt(new AuthorizationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new UpdateTokenFilter(jwtUtils), AuthorizationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
