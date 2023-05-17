package com.example.code.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.example.code.security.SecurityConfig.PERMIT_ALL_PATHS;
import static com.example.code.security.utils.JwtUtils.JwtUtils.SECRET_KEY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (Arrays.asList(PERMIT_ALL_PATHS).contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
        } else {
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            final String preTokenValue = "Bearer ";

            if (authorizationHeader != null && authorizationHeader.startsWith(preTokenValue)) {
                final String token = authorizationHeader.substring(preTokenValue.length());
                final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                final DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
                final String username = decodedJWT.getSubject();
                final String[] jwtAuthorities = decodedJWT.getClaim("authorities").asArray(String.class);
                final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                Arrays.stream(jwtAuthorities).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }
    }
}
