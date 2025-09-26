package com.example.code.deligators;

import com.example.code.model.dto.web.request.RequestLogIn;
import com.example.code.model.exceptions.UserAlreadyExistException;
import com.example.code.model.modelUtils.Role;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import com.example.code.services.AuthService.AuthService;
import com.example.code.services.UserService.UserService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoginDeligator implements JavaDelegate {
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;


    public LoginDeligator(AuthService authService, UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");
        String password = (String) delegateExecution.getVariable("password");
        RequestLogIn requestLogIn = new RequestLogIn(username, password);

        try {
            authService.checkUserExist(requestLogIn.getUsername());
            throw new BpmnError("AUTH_ERROR");
        } catch (UserAlreadyExistException e) {
            if (passwordEncoder.encode(requestLogIn.getPassword()).equals(userService.getUser(username).getPassword())) {
                Role userRole = authService.findUserRole(requestLogIn.getUsername());
                delegateExecution.setVariable("jwt-access", jwtUtils.createJWTAccessToken(username, userRole));
                delegateExecution.setVariable("jwt-refresh", jwtUtils.createJWTRefreshToken(username));
            } else {
                throw new BpmnError("AUTH_ERROR");
            }
        }
    }
}
