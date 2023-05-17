package com.example.code.model.mappers;

import com.example.code.model.dto.request.RequestRegister;
import com.example.code.model.dto.response.ResponseUser;
import com.example.code.model.entities.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {
    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    UserInfo toUserInfo(RequestRegister requestRegister);

    ResponseUser toResponseUser(UserInfo userInfo);
}
