package com.example.code.model.dto.request;

import com.example.code.model.modelUtils.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegister implements Serializable {
    private String username;

    private String password;

    private Role role;
}
