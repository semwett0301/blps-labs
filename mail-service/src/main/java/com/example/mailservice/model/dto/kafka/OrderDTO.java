package com.example.mailservice.model.dto.kafka;


import com.example.mailservice.model.modelUtils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDTO {
    private int number;

    private String username;

    private OrderStatus orderStatus;

    private int day;

    private int startTime;

    private int endTime;

    public String toEmail() {
        String email =
                "<div>" +
                        "<h1>Номер заказа: " + number + "</h4>" +
                        "<h2>Статус заказа: " + orderStatus.toString() + "</h2>" +
                        "<h3>День: " + day + "</h3>" +
                        "<h3>Период доставки: " + startTime + "-" + endTime + "</h3>" +
                        "</div>";
        email = "<div>" + email + "</div>";

        return email;
    }
}
