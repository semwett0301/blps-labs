package com.example.code.api;

import com.example.code.model.dto.request.RequestLogIn;
import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.dto.response.ResponseUser;
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

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseUser> login(@RequestBody RequestLogIn requestLogIn) {
        return ResponseEntity.ok(authService.logIn(requestLogIn));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseUser> register(@RequestBody RequestRegister requestRegister) {
        return ResponseEntity.ok(authService.register(requestRegister));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie deleteAccessCookie = ResponseCookie
                .from("access-token", null)
                .build();
        ResponseCookie deleteRefreshCookie = ResponseCookie
                .from("refresh-token", null)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString(), deleteRefreshCookie.toString())
                .build();
    }
}

