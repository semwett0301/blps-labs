package com.example.code.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthorizationFilter extends BaseCustomFilter {
    public AuthorizationFilter(JwtUtils jwtUtils) {
        super(jwtUtils);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!checkAllPermittedPaths(request)) {
                Optional<DecodedJWT> accessToken = getAccessToken(request);

                if (accessToken.isPresent()) {
                    final UsernamePasswordAuthenticationToken authenticationToken = jwtUtils.getUsernamePasswordAuthTokenFromJwt(accessToken.get());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            doFilter(request, response, filterChain);
        } catch (TokenExpiredException ex) {
            response.setStatus(401);
            response.getWriter().print(ex.getMessage());
        }
    }
}
