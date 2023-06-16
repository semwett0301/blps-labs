package com.example.code.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.CookieUtils.TokenCookieUtils;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class UpdateTokenFilter extends BaseCustomFilter {
    public UpdateTokenFilter(JwtUtils jwtUtils) {
        super(jwtUtils);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!checkAllPermittedPaths(request)) {
            Optional<DecodedJWT> accessToken = getAccessToken(request);

            if (accessToken.isPresent()) {
                final String newAccessToken = jwtUtils.createJWTAccessToken(accessToken.get().getSubject(), Role.valueOf(accessToken.get().getClaim("authorities").as(String.class)));
                final String newRefreshToken = jwtUtils.createJWTRefreshToken(accessToken.get().getSubject());

                response.addHeader(HttpHeaders.SET_COOKIE, TokenCookieUtils.createAccessTokenResponseCookie(newAccessToken).toString());
                response.addHeader(HttpHeaders.SET_COOKIE, TokenCookieUtils.createRefreshTokenResponseCookie(newRefreshToken).toString());
            }
        }

        doFilter(request, response, filterChain);
    }
}
