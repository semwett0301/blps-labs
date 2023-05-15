package com.example.code.security;

import com.example.code.filter.AuthenticationFilter;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationFilter authFilter = new AuthenticationFilter(jwtUtils, authenticationManagerBean());
        authFilter.setFilterProcessesUrl("/auth/login");

        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.authorizeRequests().antMatchers("/auth/login/**", "/auth/registration/**", "/swagger-ui.html");

        http.authorizeRequests().antMatchers("/orders/acceptance/**").hasAuthority(Role.COURIER.name());
        http.authorizeRequests().antMatchers(PATCH, "/orders/*").hasAuthority(Role.COURIER.name());

        http.authorizeRequests().antMatchers(POST, "/orders").hasAuthority(Role.USER.name());
        http.authorizeRequests().antMatchers("/orders/time/**").hasAuthority(Role.USER.name());
        http.authorizeRequests().antMatchers(GET, "/books").hasAuthority(Role.USER.name());

        http.authorizeRequests().antMatchers(GET, "/orders").hasAnyAuthority(Role.COURIER.name(), Role.USER.name());
        http.authorizeRequests().antMatchers(GET, "/orders/*").hasAnyAuthority(Role.COURIER.name(), Role.USER.name());
        http.authorizeRequests().antMatchers(DELETE, "/orders/*").hasAnyAuthority(Role.COURIER.name(), Role.USER.name());

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(authFilter);
//        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
