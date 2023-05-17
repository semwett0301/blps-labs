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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(jwtUtils.createJWTAccessToken(requestLogIn.getUsername(), userRole)).toString())
                .body(new ResponseUserAuthorized(userRole, jwtUtils.createJWTRefreshToken(requestLogIn.getUsername())));
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
    public ResponseEntity<ResponseUserAuthorized> refreshToken(@RequestBody String refreshToken) {
        DecodedJWT decodedRefreshToken = jwtUtils.getDecodedJwt(refreshToken);

        Role userRole = authService.findUserRole(decodedRefreshToken.getSubject());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(jwtUtils.createJWTAccessToken(decodedRefreshToken.getSubject(), userRole)).toString())
                .body(new ResponseUserAuthorized(userRole, jwtUtils.createJWTRefreshToken(decodedRefreshToken.getSubject())));
    }

    private ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie
                .from("access-token", token)
                .path("/")
                .httpOnly(true)
                .build();
    }
}

