package com.example.code.model.mappers;

import com.example.code.model.dto.web.request.RequestRegister;
import com.example.code.model.dto.web.response.ResponseUserAuthorized;
import com.example.code.model.entities.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {
    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    UserInfo toUserInfo(RequestRegister requestRegister);

    @Mappings({
            @Mapping(source = "userInfo.role", target = "role"),
    })
    ResponseUserAuthorized toResponseUser(UserInfo userInfo);
}
