package com.example.code.model.mappers;

import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    ResponseAvailableBook toResponseAvailableBookDTO(Book book);
}
