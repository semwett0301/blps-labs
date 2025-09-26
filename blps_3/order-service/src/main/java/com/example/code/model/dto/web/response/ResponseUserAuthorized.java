package com.example.code.model.dto.web.response;

import com.example.code.model.modelUtils.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ResponseUserAuthorized {
    private Role role;
}
