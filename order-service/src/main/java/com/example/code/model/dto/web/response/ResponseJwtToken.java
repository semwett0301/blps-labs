package com.example.code.model.dto.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseJwtToken {
    private String access_token;
    private String refresh_token;
}
