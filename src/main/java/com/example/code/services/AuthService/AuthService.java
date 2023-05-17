package com.example.code.services.AuthService;

import com.example.code.model.dto.request.RequestLogIn;
import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.dto.response.ResponseUser;

public interface AuthService {
    ResponseUser register(RequestRegister requestRegister);

    ResponseUser logIn(RequestLogIn requestLogIn);
}
