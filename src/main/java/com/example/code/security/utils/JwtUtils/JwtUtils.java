package com.example.code.security.utils.JwtUtils;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface JwtUtils {
    String createJWAccessToken(HttpServletRequest request, Authentication authentication);

    String createJWTRefreshToken(HttpServletRequest request, Authentication authentication);
}
