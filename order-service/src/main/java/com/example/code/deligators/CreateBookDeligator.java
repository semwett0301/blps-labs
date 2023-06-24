package com.example.code.deligators;

import com.example.code.model.dto.web.request.RequestCreateOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.modelUtils.ReservedBook;
import com.example.code.model.modelUtils.Role;
import com.example.code.services.AuthService.AuthService;
import com.example.code.services.DeliveryService.DeliveryService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CreateBookDeligator implements JavaDelegate {
    private final DeliveryService deliveryService;
    private final AuthService authService;

    @Autowired
    public CreateBookDeligator(DeliveryService deliveryService, AuthService authService) {
        this.deliveryService = deliveryService;
        this.authService = authService;
    }


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String username = (String) delegateExecution.getVariable("username");
        String bookId = (String) delegateExecution.getVariable("book_id");
        String amount = (String) delegateExecution.getVariable("amount");
        Integer day = (Integer) delegateExecution.getVariable("day");

        List<ReservedBook> reservedBookList = new ArrayList<>();
        reservedBookList.add(new ReservedBook(UUID.fromString(bookId), Integer.parseInt(amount)));

        RequestCreateOrder requestCreateOrder = new RequestCreateOrder(reservedBookList, day);

        try {
            if (Objects.equals(authService.findUserRole(username), Role.USER)) {
                Order order = deliveryService.createOrder(requestCreateOrder.getDay(), requestCreateOrder.getBooks(), username);
                delegateExecution.setVariable("order_id", order.getNumber());
            } else {
                throw new BpmnError("ACCESS_ERROR");
            }
        } catch (BookIsNotAvailableException e) {
            throw new BpmnError("CREATE_BOOK_ERROR");
        }
    }
}
