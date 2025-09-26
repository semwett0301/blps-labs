package com.example.mailservice.model.dto.kafka;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private int number;

    private String username;

    private String orderStatus;

    private int day;

    private int startTime;

    private int endTime;

    public String toEmail() {
        String email =
                "<div>" +
                        "<h1>Номер заказа: " + number + "</h1>" +
                        "<h2>Статус заказа: " + orderStatus + "</h2>" +
                        "<div>День: " + day + "</div>" +
                        "<div>Период доставки: " + startTime + "-" + endTime + "</div>" +
                        "</div>";
        email = "<div>" + email + "</div>";

        return email;
    }
}
