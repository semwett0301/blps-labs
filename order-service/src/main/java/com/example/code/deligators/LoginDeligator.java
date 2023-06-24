package com.example.code.deligators;

import com.example.code.model.dto.web.request.RequestLogIn;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import com.example.code.services.AuthService.AuthService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class LoginDeligator implements JavaDelegate {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public LoginDeligator(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");
        String password = (String) delegateExecution.getVariable("password");
        RequestLogIn requestLogIn = new RequestLogIn(username, password);
        Role userRole = authService.findUserRole(requestLogIn.getUsername());
        delegateExecution.setVariable("jwt-access",jwtUtils.createJWTAccessToken(username,userRole));
        delegateExecution.setVariable("jwt-refresh",jwtUtils.createJWTRefreshToken(username));
    }
}
