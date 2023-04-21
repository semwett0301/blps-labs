package com.example.code.services.WarehouseService;

import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.mappers.BookMapper;
import com.example.code.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceLitRes implements WarehouseService{

    private final BookRepository bookRepository;

    @Autowired
    public WarehouseServiceLitRes(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<ResponseAvailableBook> getAllAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailableAmount() > book.getReservations().size())
                .map(BookMapper.INSTANCE::toResponseAvailableBookDTO)
                .collect(Collectors.toList());
    }
}
