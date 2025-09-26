package com.example.code.model.mappers;

import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mappings({
            @Mapping(source = "book.id", target = "id"),
            @Mapping(source = "book.name", target = "name"),
            @Mapping(source = "book.description", target = "description")
    })
    ResponseAvailableBook toResponseAvailableBook(Book book);
}
