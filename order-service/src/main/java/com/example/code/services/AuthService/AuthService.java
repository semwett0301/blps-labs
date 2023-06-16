package com.example.code.services.AuthService;

import com.example.code.model.dto.web.request.RequestRegister;
import com.example.code.model.exceptions.UserAlreadyExistException;
import com.example.code.model.modelUtils.Role;

public interface AuthService {
    void register(RequestRegister requestRegister);

    Role findUserRole(String username);

    void checkUserExist(String username) throws UserAlreadyExistException;
}
