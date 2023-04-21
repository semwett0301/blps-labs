package com.example.code.api;

import com.example.code.model.dto.RequestReservedBook;
import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.services.WarehouseService.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final WarehouseService warehouseService;

    @Autowired
    public BookController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<List<ResponseAvailableBook>> getAvailableBooks() {
        return ResponseEntity.ok().body(warehouseService.getAllAvailableBooks());
    }

    @PostMapping
    public ResponseEntity<List<RequestReservedBook>> reserveBooks(@RequestBody List<RequestReservedBook> reservedBookList) {
        try {
            warehouseService.reserveBooks(reservedBookList);
            return ResponseEntity.ok().body(reservedBookList);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BookIsNotAvailableException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
