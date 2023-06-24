package com.example.code.deligators;

import com.example.code.model.dto.web.request.RequestRegister;
import com.example.code.model.modelUtils.Role;
import com.example.code.services.AuthService.AuthService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationDeligator implements JavaDelegate {

    private final AuthService authService;

    @Autowired
    public RegistrationDeligator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");
        String password = (String) delegateExecution.getVariable("password");
        String email = (String) delegateExecution.getVariable("email");
        String roleString = (String) delegateExecution.getVariable("role");

        authService.checkUserExist(username);
        authService.register(new RequestRegister(username, password, Role.valueOf(roleString), email));
    }
}
