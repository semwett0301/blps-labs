package com.example.code.filter;

import com.example.code.model.dto.response.ResponseJwtToken;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        final String access = jwtUtils.createJWAccessToken(request, authResult);
        final String refresh = jwtUtils.createJWTRefreshToken(request, authResult);

        Cookie jwtAccessTokenCookie = new Cookie("access-token", access);
        jwtAccessTokenCookie.setSecure(true);
        jwtAccessTokenCookie.setHttpOnly(true);
        jwtAccessTokenCookie.setDomain("localhost:8080");

        Cookie jwtRefreshTokenCookie = new Cookie("refresh-token", refresh);
        jwtRefreshTokenCookie.setSecure(true);
        jwtRefreshTokenCookie.setHttpOnly(true);
        jwtRefreshTokenCookie.setDomain("localhost:8080");

        response.addCookie(jwtAccessTokenCookie);
        response.addCookie(jwtRefreshTokenCookie);
    }
}
