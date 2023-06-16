package com.example.code.model.dto.request;

import com.example.code.model.modelUtils.ReservedBook;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RequestCreateOrder {
    List<ReservedBook> books;
    int day;
}


