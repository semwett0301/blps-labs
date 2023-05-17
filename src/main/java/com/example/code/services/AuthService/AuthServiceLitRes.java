package com.example.code.services.AuthService;

import com.example.code.model.dto.request.RequestLogIn;
import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.dto.response.ResponseUser;
import com.example.code.model.entities.UserInfo;
import com.example.code.model.mappers.UserInfoMapper;
import com.example.code.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceLitRes implements AuthService {

    private UserRepository userRepository;

    @Autowired
    public AuthServiceLitRes(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseUser register(RequestRegister requestRegister) {
        UserInfo newUserInfo = UserInfoMapper.INSTANCE.toUserInfo(requestRegister);
        userRepository.save(newUserInfo);

        return UserInfoMapper.INSTANCE.toResponseUser(newUserInfo);
    }

    @Override
    public ResponseUser logIn(RequestLogIn requestLogIn) {
        UserInfo userInfo = userRepository.findByUsername(requestLogIn.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User wasn't found"));
        return UserInfoMapper.INSTANCE.toResponseUser(userInfo);
    }
}
