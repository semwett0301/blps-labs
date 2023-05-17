package com.example.code.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.model.dto.request.RequestLogIn;
import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.dto.response.ResponseUserAuthorized;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import com.example.code.services.AuthService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseUserAuthorized> login(@RequestBody RequestLogIn requestLogIn) {
        Role userRole = authService.findUserRole(requestLogIn.getUsername());

        return getResponseAuthorizedEntity(requestLogIn.getUsername(), userRole);
    }

    @PostMapping("/register")
    public void register(@RequestBody RequestRegister requestRegister) throws UserNotFoundException {
        authService.register(requestRegister);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(null).toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseUserAuthorized> refreshToken(@CookieValue("refresh-token") String refreshToken) {
        DecodedJWT decodedRefreshToken = jwtUtils.getDecodedJwt(refreshToken);

        Role userRole = authService.findUserRole(decodedRefreshToken.getSubject());
        return getResponseAuthorizedEntity(decodedRefreshToken.getSubject(), userRole);
    }

    private ResponseEntity<ResponseUserAuthorized> getResponseAuthorizedEntity(String username, Role role) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(jwtUtils.createJWTAccessToken(username, role)).toString(),
                        createRefreshTokenCookie(jwtUtils.createJWTRefreshToken(username)).toString())
                .body(new ResponseUserAuthorized(role));
    }

    private ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie
                .from("access-token", token)
                .path("/")
                .httpOnly(true)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie
                .from("refresh-token", token)
                .path("/auth/refresh")
                .httpOnly(true)
                .build();
    }
}

