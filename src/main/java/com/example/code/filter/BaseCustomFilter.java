package com.example.code.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static com.example.code.security.SecurityConfig.PERMIT_ALL_PATHS;

public abstract class BaseCustomFilter extends OncePerRequestFilter {
    protected final JwtUtils jwtUtils;

    public BaseCustomFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    protected boolean checkAllPermittedPaths(HttpServletRequest request) {
        return Arrays.asList(PERMIT_ALL_PATHS).contains(request.getServletPath());
    }
    protected Optional<DecodedJWT> getAccessToken(HttpServletRequest request) {
        return getToken(request, "access-token");
    }

    private Optional<DecodedJWT> getToken(HttpServletRequest request, String name) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    token = cookie.getValue();
                }
            }
        }
        return Optional.ofNullable(jwtUtils.getDecodedJwt(token));
    }
}
