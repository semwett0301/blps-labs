package com.example.code.security.utils.JwtUtils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.code.model.modelUtils.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtUtilsImpl implements JwtUtils {
    private final String SECRET_KEY = "aPdSgVkYp3s6v9y$B&E)H+MbQeThWmZq4t7w!z%C*F-JaNcRfUjXn2r5u8x/A?D(G+KbPeSgVkYp3s6v9y$B&E)H@McQfTjWmZq4t7w!z%C*F-JaNdRgUkXp2r5u8x/A?D(G+KbPeShVmYq3t6v9y$B&E)H@McQfTjWnZr4u7x!z%C*F-JaNdRgUkXp2s5v8y/B?D(G+KbPeShVmYq3t6w9z$C&F)H@McQfTjWnZr4u7x!A%D*G-KaNdRgUkXp2s5v8y/B?E(H+MbQeShVmYq3t6w9z$C&F)J@NcRfUjWnZr4u7x!A%D*G-KaPdSgVkYp2s5v8y/B?E(H+MbQeThWmZq4t6w9z$C&F)J@NcRfUjXn2r5u8x!A%D*G-KaPdSgVkYp3s6v9y$B?E(H+MbQeThWmZq4t7w!z%C*F)J@NcRfUjXn2r5u8x/A?D(G+KaPdSgVkYp3s6v9y$B&E)H@McQeThWmZq4t7w!z%C*F-JaNdRgUkXn2r5u8x/A?D(G+";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    @Override
    public String createJWTAccessToken(String username, Role role) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 1000))
                .withClaim("authorities", role.getRole())
                .sign(algorithm);
    }

    @Override
    public String createJWTRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000 * 60 * 100))
                .sign(algorithm);
    }

    @Override
    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthTokenFromJwt(DecodedJWT jwtToken) {
        return new UsernamePasswordAuthenticationToken(jwtToken.getSubject(), null, getAuthorities(jwtToken));
    }

    @Override
    public DecodedJWT getDecodedJwt(String token) {
        return JWT.require(algorithm).build().verify(token);
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(DecodedJWT decodedJWT) {
        final String jwtAuthority = decodedJWT.getClaim("authorities").asString();
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(jwtAuthority));
        return authorities;
    }
}
