package com.example.code.model.converters;

import com.example.code.model.modelUtils.Role;
import org.springframework.core.convert.converter.Converter;

import javax.validation.constraints.NotNull;

public class StringToRoleConverter implements Converter<String, Role> {
    @Override
    public Role convert(@NotNull String source) {
        return Role.valueOf(source.toUpperCase());
    }
}
