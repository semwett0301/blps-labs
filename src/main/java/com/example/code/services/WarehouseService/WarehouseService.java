package com.example.code.services.WarehouseService;

import com.example.code.model.dto.ResponseAvailableBook;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WarehouseService {
    List<ResponseAvailableBook> getAllAvailableBooks();
}
