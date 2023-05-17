package com.example.code.services.AuthService;

import com.example.code.model.dto.request.RequestLogIn;
import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.modelUtils.Role;

public interface AuthService {
    void register(RequestRegister requestRegister) throws UserNotFoundException;

    Role findUserRole(String username);
}
