package com.example.code.model.modelUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Role {
    COURIER("COURIER"),
    USER("USER");

    private final String role;

    @JsonValue
    public String getRole() {
        return role;
    }



    @JsonCreator
    public static Role getRoleFromString(String value) {
        for (Role dep : Role.values()) {
            if (dep.getRole().equals(value)) {
                return dep;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return role;
    }
}
