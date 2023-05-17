package com.example.code.security.utils.JwtUtils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.model.modelUtils.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface JwtUtils {
    String createJWTAccessToken(String username, Role role);

    String createJWTRefreshToken(String username);

    UsernamePasswordAuthenticationToken getUsernamePasswordAuthTokenFromJwt(DecodedJWT jwtToken);

    Optional<DecodedJWT> getDecodedJwt(String token);
}
