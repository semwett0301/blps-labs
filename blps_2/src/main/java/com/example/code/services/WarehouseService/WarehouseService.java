package com.example.code.services.WarehouseService;

import com.example.code.model.dto.response.ResponseAvailableBook;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.modelUtils.ReservedBook;

import java.util.List;

public interface WarehouseService {
    List<ResponseAvailableBook> getAllAvailableBooks();
    void reserveBooks(List<ReservedBook> reservedBookList, Order order) throws UserNotFoundException, BookIsNotAvailableException;

    void removeReservation(Integer orderId);
}
