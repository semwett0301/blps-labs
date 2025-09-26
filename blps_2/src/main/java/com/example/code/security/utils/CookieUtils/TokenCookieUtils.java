package com.example.code.security.utils.CookieUtils;

import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;

public class TokenCookieUtils {

    private static final String ACCESS_PATH = "/";
    private static final String REFRESH_PATH = "/auth/refresh";

    public static final String ACCESS_NAME = "access-token";
    public static final String REFRESH_NAME = "refresh-token";

    public static ResponseCookie createAccessTokenResponseCookie(String token) {
        return ResponseCookie.from(ACCESS_NAME, token)
                .path(ACCESS_PATH)
                .httpOnly(true)
                .build();
    }

    public static ResponseCookie createRefreshTokenResponseCookie(String token) {
        return ResponseCookie.from(REFRESH_NAME, token)
                .path(REFRESH_PATH)
                .httpOnly(true)
                .build();
    }
}