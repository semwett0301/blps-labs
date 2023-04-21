package com.example.code.services.WarehouseService;

import com.example.code.model.dto.RequestReservedBook;
import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WarehouseService {
    List<ResponseAvailableBook> getAllAvailableBooks();
    void reserveBooks(List<RequestReservedBook> reservedBookList) throws UserNotFoundException, BookIsNotAvailableException;
}
