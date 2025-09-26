package com.example.code.model.dto;

import com.example.code.model.modelUtils.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseAvailableTime {
    private List<TimePeriod> periods;
}
