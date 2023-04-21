package com.example.code.services.WarehouseService;

import com.example.code.model.dto.RequestReservedBook;
import com.example.code.model.dto.ResponseAvailableBook;
import com.example.code.model.entities.Book;
import com.example.code.model.entities.Reservation;
import com.example.code.model.entities.User;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.model.mappers.BookMapper;
import com.example.code.repositories.BookRepository;
import com.example.code.repositories.ReservationRepository;
import com.example.code.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceLitRes implements WarehouseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public WarehouseServiceLitRes(BookRepository bookRepository, UserRepository userRepository, ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<ResponseAvailableBook> getAllAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailableAmount() > book.getReservations().size())
                .map(BookMapper.INSTANCE::toResponseAvailableBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void reserveBooks(List<RequestReservedBook> reservedBookList) throws UserNotFoundException, BookIsNotAvailableException {
        final UUID userId = UUID.fromString("48f68268-656c-49ff-a220-df39bb9f8241");

        User user = getUserById(userId);
        List<BookReservation> bookReservations = createBookReservationList(reservedBookList);
        decreaseAvailableAmountOfBooks(bookReservations);
        createReservations(bookReservations, user);
        updateBooks(bookReservations);
    }

    private List<BookReservation> createBookReservationList(List<RequestReservedBook> requestReservedBooks) throws BookIsNotAvailableException {
        List<BookReservation> bookReservations = new ArrayList<>();
        List<Book> books = getRequestedBooks(requestReservedBooks);

        for (Book book : books) {
            final int requestedAmount = requestReservedBooks.stream()
                    .filter(e -> e.getId().equals(book.getId()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getAmount();
            bookReservations.add(new BookReservation(book, requestedAmount));
        }

        return bookReservations;
    }

    private void updateBooks(List<BookReservation> bookReservations) {
        bookRepository.saveAll(bookReservations.stream()
                .map(BookReservation::getBook)
                .collect(Collectors.toList()));
    }

    private void createReservations(List<BookReservation> bookReservations, User user) {
        List<Reservation> reservations = new ArrayList<>();
        bookReservations.forEach(bookReservation -> {
            for (int i = 0; i < bookReservation.getRequestedAmount(); i++) {
                reservations.add(new Reservation(bookReservation.getBook(), user));
            }
        });
        reservationRepository.saveAll(reservations);
    }

    private void decreaseAvailableAmountOfBooks(List<BookReservation> bookReservations) {
        bookReservations.forEach(bookReservation -> {
            Book book = bookReservation.getBook();
            book.setAvailableAmount(book.getAvailableAmount() - bookReservation.getRequestedAmount());
        });
    }

    private List<Book> getRequestedBooks(List<RequestReservedBook> reservedBookList) throws BookIsNotAvailableException {
        List<Book> books = getBooksByIds(reservedBookList.stream().map(RequestReservedBook::getId).collect(Collectors.toList()));
        if (reservedBookList.size() != books.size()) {
            throw new BookIsNotAvailableException();
        } else {
            return books;
        }
    }

    private List<Book> getBooksByIds(List<UUID> listIds) {
        return bookRepository.findAll().stream()
                .filter(book -> listIds.contains(book.getId()))
                .collect(Collectors.toList());
    }

    private User getUserById(UUID userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}

@AllArgsConstructor
@Getter
@Setter
class BookReservation {
    private Book book;
    private int requestedAmount;
}
